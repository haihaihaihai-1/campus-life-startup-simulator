const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

function crc32(buf) { let c=0xFFFFFFFF,t=[]; for(let i=0;i<256;i++){let k=i;for(let j=0;j<8;j++)k=(k&1)?0xEDB88320^(k>>>1):k>>>1;t[i]=k;} for(let i=0;i<buf.length;i++)c=t[(c^buf[i])&0xFF]^(c>>>8); return (c^0xFFFFFFFF)>>>0; }
function chunk(type, data) { const len=Buffer.alloc(4); len.writeUInt32BE(data.length); const t=Buffer.from(type); const cd=Buffer.concat([t,data]); const c=Buffer.alloc(4); c.writeUInt32BE(crc32(cd)); return Buffer.concat([len,t,data,c]); }
function makePNG(size, fn) {
  const sig=Buffer.from([137,80,78,71,13,10,26,10]); const ihdr=Buffer.alloc(13);
  ihdr.writeUInt32BE(size,0); ihdr.writeUInt32BE(size,4); ihdr[8]=8; ihdr[9]=6;
  const raw=Buffer.alloc(size*size*4);
  for(let y=0;y<size;y++) for(let x=0;x<size;x++){ const [r,g,b,a]=fn(x,y); const i=(y*size+x)*4; raw[i]=r;raw[i+1]=g;raw[i+2]=b;raw[i+3]=a; }
  const scan=Buffer.alloc((size*4+1)*size);
  for(let y=0;y<size;y++){ scan[y*(size*4+1)]=0; raw.copy(scan, y*(size*4+1)+1, y*size*4, (y+1)*size*4); }
  return Buffer.concat([sig, chunk('IHDR',ihdr), chunk('IDAT',zlib.deflateSync(scan)), chunk('IEND',Buffer.alloc(0))]);
}

// 纹理函数辅助
function noise(x,y,seed) { return ((x*73856093)^(y*19349663)^seed)>>>0; }
function edge(x,y,size,base,dark){ if(x===0||y===0||x===size-1||y===size-1) return dark; return base; }
function tint(c, dr, dg, db){ return [Math.max(0,Math.min(255,c[0]+dr)), Math.max(0,Math.min(255,c[1]+dg)), Math.max(0,Math.min(255,c[2]+db)), c[3]]; }

const blockDir = 'src/main/resources/assets/campuslife/textures/block';
const itemDir = 'src/main/resources/assets/campuslife/textures/item';
const guiDir = 'src/main/resources/assets/campuslife/textures/gui';
fs.mkdirSync(blockDir,{recursive:true}); fs.mkdirSync(itemDir,{recursive:true}); fs.mkdirSync(guiDir,{recursive:true});

const blockTextures = {
  // talent_desk - 现代办公风（深蓝+银白屏幕）
  'talent_desk_base': (x,y) => { const n=noise(x,y,1)%8; return [40+n, 50+n, 80+n, 255]; },
  'talent_desk_screen': (x,y) => {
    if (x===0||y===0||x===15||y===15) return [20,20,30,255];
    if ((x>=3&&x<=12) && y%3===0) return [80,160,220,255];
    return [60,120,180,255];
  },
  'talent_desk_top': (x,y) => { const n=noise(x,y,2)%6; return [50+n, 60+n, 90+n, 255]; },
  // notary_terminal - 区块链终端（黑色+绿色LED）
  'notary_case': (x,y) => { const n=noise(x,y,3)%5; return [25+n, 25+n, 30+n, 255]; },
  'notary_led': (x,y) => {
    if ((x+y)%3===0) return [40,255,80,255];
    if (x===0||y===0||x===15||y===15) return [20,80,30,255];
    return [30,120,50,255];
  },
  'notary_top': (x,y) => { const n=noise(x,y,4)%5; return [35+n, 35+n, 40+n, 255]; },
  // carbon_panel - 碳交易（绿色环保+太阳能板）
  'carbon_base': (x,y) => { const n=noise(x,y,5)%4; return [80+n, 80+n, 80+n, 255]; },
  'carbon_leaf': (x,y) => {
    const cx=8,cy=8; const dx=x-cx, dy=y-cy;
    if (dx*dx+dy*dy < 25) return [60,180,60,255];
    if (dx*dx+dy*dy < 49) return [40,140,40,255];
    return [30,100,30,255];
  },
  'carbon_screen': (x,y) => {
    if (y%4===0) return [40,200,80,255];
    return [20,60,30,255];
  },
  // supply_factory - 工厂（钢铁灰+橙色管道）
  'supply_wall': (x,y) => {
    const n=noise(x,y,6)%6;
    if (y%4===0) return [100+n, 100+n, 110+n, 255];
    return [80+n, 80+n, 90+n, 255];
  },
  'supply_pipe': (x,y) => {
    const cx=8; const dx=x-cx;
    if (Math.abs(dx)<3) return [200,100,40,255];
    return [180,80,30,255];
  },
  'supply_top': (x,y) => { const n=noise(x,y,7)%5; return [90+n, 90+n, 100+n, 255]; },
  // franchise_sign - 加盟店招牌（金黄+红色品牌色）
  'franchise_pole': (x,y) => { const n=noise(x,y,8)%4; return [120+n, 100+n, 60+n, 255]; },
  'franchise_sign': (x,y) => {
    if (x===0||y===0||x===15||y===15) return [180,30,30,255];
    if ((y===6||y===9) && x>=2&&x<=13) return [240,220,40,255];
    return [220,40,40,255];
  },
  // tax_office - 税务局（米色墙+红色印章）
  'tax_wall': (x,y) => { const n=noise(x,y,9)%6; return [200+n, 190+n, 160+n, 255]; },
  'tax_seal': (x,y) => {
    const cx=5,cy=3,dx=x-cx,dy=y-cy;
    if (dx*dx+dy*dy<9) return [220,40,40,255];
    return [240,220,200,255];
  },
  'tax_top': (x,y) => { const n=noise(x,y,10)%5; return [180+n, 170+n, 140+n, 255]; },
  // training_podium - 培训讲台（木色+绿黑板）
  'training_base': (x,y) => { const n=noise(x,y,11)%7; return [120+n, 80+n, 40+n, 255]; },
  'training_board': (x,y) => {
    if (x===0||y===0||x===15||y===15) return [80,50,20,255];
    if ((x===4&&y>=4&&y<=11) || (y===6&&x>=3&&x<=8)) return [240,240,240,255];
    return [30,60,40,255];
  },
  'training_top': (x,y) => { const n=noise(x,y,12)%6; return [140+n, 100+n, 50+n, 255]; },
  // court_bench - 法庭审判台（深红木+金锤）
  'court_wood': (x,y) => { const n=noise(x,y,13)%6; return [80+n, 30+n, 30+n, 255]; },
  'court_gavel': (x,y) => { const n=noise(x,y,14)%4; return [200+n, 160+n, 40+n, 255]; },
  'court_top': (x,y) => { const n=noise(x,y,15)%5; return [100+n, 40+n, 40+n, 255]; }
};

for (const [name, fn] of Object.entries(blockTextures)) {
  fs.writeFileSync(path.join(blockDir, name + '.png'), makePNG(16, fn));
}

// 物品纹理（item form of block）
const itemTextures = {
  'talent_desk': (x,y) => { if(y<6)return [40,50,80,255]; return [50,60,90,255]; },
  'notary_terminal': (x,y) => { if(y<4)return [40,200,60,255]; return [25,25,30,255]; },
  'carbon_panel': (x,y) => { const cx=8,cy=8,dx=x-cx,dy=y-cy; if(dx*dx+dy*dy<25) return [60,180,60,255]; return [80,80,80,255]; },
  'supply_factory': (x,y) => { if(x>=6&&x<=9&&y<6)return [200,100,40,255]; return [90,90,100,255]; },
  'franchise_sign': (x,y) => { if(y<10)return [220,40,40,255]; return [120,100,60,255]; },
  'tax_office': (x,y) => { const cx=8,cy=8,dx=x-cx,dy=y-cy; if(dx*dx+dy*dy<16) return [220,40,40,255]; return [200,190,160,255]; },
  'training_podium': (x,y) => { if(y<10)return [30,60,40,255]; return [120,80,40,255]; },
  'court_bench': (x,y) => { if(x>=6&&x<=9&&y<6) return [220,180,40,255]; return [80,30,30,255]; }
};
for (const [name, fn] of Object.entries(itemTextures)) {
  fs.writeFileSync(path.join(itemDir, name + '.png'), makePNG(16, fn));
}

// GUI 背景 (256x256 但内容只在 176x200 区域)
const guiBgs = {
  'talent': [40, 50, 80],
  'notary': [25, 50, 30],
  'carbon': [30, 70, 40],
  'supply': [70, 60, 50],
  'franchise': [80, 40, 40],
  'tax': [180, 160, 130],
  'training': [50, 70, 40],
  'court': [60, 30, 30]
};
for (const [name, [r,g,b]] of Object.entries(guiBgs)) {
  const png = makePNG(256, (x,y) => {
    if (x<176 && y<200) {
      if (x===0||x===175||y===0||y===199) return [r-20,g-20,b-20,255];
      const n=noise(x,y,99)%6;
      return [r+n, g+n, b+n, 250];
    }
    return [0,0,0,0];
  });
  fs.writeFileSync(path.join(guiDir, name + '.png'), png);
}

console.log('v18 纹理生成完成: 24 方块纹理 + 8 物品纹理 + 8 GUI背景');
