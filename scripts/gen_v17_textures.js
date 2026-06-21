#!/usr/bin/env node
// v17 textures - generate noise+edge+highlight pixel art for 8 new blocks
const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

function crc32(buf){let c=0xFFFFFFFF,t=[];for(let i=0;i<256;i++){let k=i;for(let j=0;j<8;j++)k=(k&1)?0xEDB88320^(k>>>1):k>>>1;t[i]=k;}for(let i=0;i<buf.length;i++)c=t[(c^buf[i])&0xFF]^(c>>>8);return (c^0xFFFFFFFF)>>>0;}
function chunk(type,data){const len=Buffer.alloc(4);len.writeUInt32BE(data.length);const t=Buffer.from(type);const cd=Buffer.concat([t,data]);const c=Buffer.alloc(4);c.writeUInt32BE(crc32(cd));return Buffer.concat([len,t,data,c]);}
function makePNG(size, fn){
  const sig=Buffer.from([137,80,78,71,13,10,26,10]);
  const ihdr=Buffer.alloc(13);
  ihdr.writeUInt32BE(size,0);ihdr.writeUInt32BE(size,4);ihdr[8]=8;ihdr[9]=6;
  const raw=Buffer.alloc(size*size*4);
  for(let y=0;y<size;y++)for(let x=0;x<size;x++){
    const [r,g,b,a]=fn(x,y);
    const i=(y*size+x)*4;
    raw[i]=r;raw[i+1]=g;raw[i+2]=b;raw[i+3]=a;
  }
  const scan=Buffer.alloc((size*4+1)*size);
  for(let y=0;y<size;y++){
    scan[y*(size*4+1)]=0;
    raw.copy(scan,y*(size*4+1)+1,y*size*4,(y+1)*size*4);
  }
  return Buffer.concat([sig,chunk('IHDR',ihdr),chunk('IDAT',zlib.deflateSync(scan)),chunk('IEND',Buffer.alloc(0))]);
}

// noise/edge/highlight helpers
function noise(x,y,seed){
  const v = Math.sin(x*12.9898+y*78.233+seed)*43758.5453;
  return ((v - Math.floor(v))*2-1);
}
function clamp(v){return Math.max(0,Math.min(255,Math.round(v)));}
function tint(base,nz,dim){
  return [clamp(base[0]+nz*20-dim),clamp(base[1]+nz*20-dim),clamp(base[2]+nz*20-dim),base[3]||255];
}

const blockDir='src/main/resources/assets/campuslife/textures/block';
const itemDir='src/main/resources/assets/campuslife/textures/item';

const textures = {
  // loan_atm
  'loan_atm_metal': (x,y)=>{
    const nz=noise(x,y,11);
    const edge=(x===0||x===15||y===0||y===15)?15:0;
    const hi=(y<2)?-20:0;
    return tint([110,115,125,255],nz,edge+hi);
  },
  'loan_atm_screen': (x,y)=>{
    const nz=noise(x,y,12)*0.5;
    if(x===0||x===15||y===0||y===15)return tint([60,80,100,255],nz,30);
    // 显示金额 "¥"
    if(x>=6&&x<=9&&y>=4&&y<=11){
      if((y===5||y===6)&&x>=6&&x<=9)return [180,220,180,255];
      if(y===7&&x===7)return [180,220,180,255];
      if(y===8&&x>=6&&x<=8)return [180,220,180,255];
      if(y>=9&&y<=10&&x===7)return [180,220,180,255];
    }
    return tint([20,80,40,255],nz,0);
  },
  'loan_atm_slot': (x,y)=>{
    if(y>=6&&y<=10)return [20,20,30,255];
    return tint([90,90,100,255],noise(x,y,13),0);
  },
  // patent_cabinet
  'patent_cabinet_wood': (x,y)=>{
    const nz=noise(x,y,21);
    const grain = Math.sin(y*0.8)*8;
    return tint([130,85,40,255],nz+grain/20,0);
  },
  'patent_cabinet_scroll': (x,y)=>{
    if(x===0||x===4||y===0||y===11)return [180,150,100,255];
    if(y===2||y===5||y===8)return [120,100,60,255];
    return [230,210,170,255];
  },
  'patent_cabinet_seal': (x,y)=>{
    const cx=2,cy=2;
    const d=Math.sqrt((x-cx)**2+(y-cy)**2);
    if(d<2)return [200,40,40,255];
    if(d<3)return [140,20,20,255];
    return [0,0,0,0];
  },
  // merger_table
  'merger_table_marble': (x,y)=>{
    const nz=noise(x,y,31)*0.7;
    const vein=(Math.sin(x*0.3+y*0.5)>0.7)?10:0;
    return tint([225,225,230,255],nz,-vein);
  },
  'merger_table_leather': (x,y)=>{
    return tint([100,60,40,255],noise(x,y,32),0);
  },
  'merger_table_docs': (x,y)=>{
    if(x===0||x===5||y===0||y===5)return [180,180,170,255];
    if(y===2||y===4)return [50,50,80,255];
    return [240,240,235,255];
  },
  // ad_billboard
  'ad_billboard_frame': (x,y)=>{
    const nz=noise(x,y,41);
    return tint([60,60,70,255],nz,5);
  },
  'ad_billboard_ad': (x,y)=>{
    // 渐变彩虹海报
    const r=Math.round(100+155*(x/15));
    const g=Math.round(100+155*(y/15));
    const b=Math.round(150+105*((15-x)/15));
    return [r,g,b,255];
  },
  'ad_billboard_back': (x,y)=>{
    return tint([80,80,90,255],noise(x,y,42),0);
  },
  // realty_kiosk
  'realty_kiosk_wood': (x,y)=>{
    const grain=Math.sin(y*0.6)*5;
    return tint([150,100,60,255],noise(x,y,51)+grain/20,0);
  },
  'realty_kiosk_map': (x,y)=>{
    const nz=noise(x,y,52);
    // 道路线
    if(x===4||x===11||y===4||y===11)return [80,80,80,255];
    // 建筑块
    if((x===2||x===14)&&y>=6&&y<=9)return [180,60,60,255];
    if((y===2||y===14)&&x>=6&&x<=9)return [60,140,60,255];
    return tint([240,220,180,255],nz,0);
  },
  'realty_kiosk_key': (x,y)=>{
    const cx=3,cy=3;
    const d=Math.sqrt((x-cx)**2+(y-cy)**2);
    if(d<2)return [220,180,40,255]; // 钥匙环
    if(d<3&&d>=2)return [180,140,30,255];
    if(y===3&&x>=3&&x<=8)return [220,180,40,255]; // 钥匙杆
    if(x===8&&y===4)return [220,180,40,255];
    if(x===9&&y===4)return [220,180,40,255];
    return [0,0,0,0];
  },
  // hr_desk
  'hr_desk_wood': (x,y)=>{
    const grain=Math.sin(y*0.7)*6;
    return tint([130,80,40,255],noise(x,y,61)+grain/20,0);
  },
  'hr_desk_screen': (x,y)=>{
    const nz=noise(x,y,62)*0.3;
    if(x===0||x===7||y===0||y===3)return [30,30,40,255];
    // 简历图标行
    if(y===1&&x>=1&&x<=2)return [180,180,200,255];
    if(y===1&&x>=4&&x<=6)return [100,100,120,255];
    if(y===2&&x>=1&&x<=6)return [80,80,100,255];
    return tint([40,80,120,255],nz,0);
  },
  'hr_desk_folder': (x,y)=>{
    if(y===0)return [180,140,40,255]; // 顶部
    if(x===0||x===3)return [160,120,30,255]; // 边
    return [220,180,80,255];
  },
  // meta_portal
  'meta_portal_frame': (x,y)=>{
    const nz=noise(x,y,71);
    return tint([40,30,70,255],nz,5);
  },
  'meta_portal_portal': (x,y)=>{
    // 紫色漩涡
    const cx=8,cy=8;
    const d=Math.sqrt((x-cx)**2+(y-cy)**2);
    const ang=Math.atan2(y-cy,x-cx);
    const swirl=Math.sin(d*0.8+ang*3)*0.5+0.5;
    const r=Math.round(100+swirl*100);
    const g=Math.round(40+swirl*60);
    const b=Math.round(160+swirl*80);
    return [r,g,b,255];
  },
  'meta_portal_rune': (x,y)=>{
    const nz=noise(x,y,72);
    // 符文图案
    if((x===2&&y===2)||(x===2&&y===13)||(x===13&&y===2)||(x===13&&y===13))return [180,140,220,255];
    if(x===y||x+y===15)return tint([100,60,160,255],nz,0);
    return tint([60,40,100,255],nz,0);
  },
  // insurance_kiosk
  'insurance_kiosk_metal': (x,y)=>{
    return tint([180,180,200,255],noise(x,y,81),(y<2?-15:0));
  },
  'insurance_kiosk_screen': (x,y)=>{
    if(x===0||x===11||y===0||y===7)return [40,60,80,255];
    // shield 图标
    if(y===2&&x>=4&&x<=6)return [100,200,255,255];
    if(y>=3&&y<=4&&x>=3&&x<=7)return [100,200,255,255];
    if(y===5&&x>=4&&x<=6)return [100,200,255,255];
    if(y===6&&x===5)return [100,200,255,255];
    return [20,40,80,255];
  },
  'insurance_kiosk_umbrella': (x,y)=>{
    const cx=3.5,cy=2;
    const d=Math.sqrt((x-cx)**2+(y-cy)**2);
    if(y<=3&&d<4)return [220,40,40,255]; // 伞面
    if(y>3&&x===3)return [80,60,40,255]; // 伞柄
    return [0,0,0,0];
  },
};

let count=0;
for(const [name,fn] of Object.entries(textures)){
  fs.writeFileSync(path.join(blockDir,name+'.png'),makePNG(16,fn));
  count++;
}
// 物品纹理（每个方块一个，复制基础纹理）
const itemBase = {
  'loan_atm': (x,y)=>{
    if(x<2||x>13||y<1||y>14)return [0,0,0,0];
    return textures['loan_atm_metal'](x,y);
  },
  'patent_cabinet': (x,y)=>{
    if(x<1||x>14||y<1||y>14)return [0,0,0,0];
    return textures['patent_cabinet_wood'](x,y);
  },
  'merger_table': (x,y)=>{
    if(y<4||y>11)return [0,0,0,0];
    if(y>=4&&y<=6)return textures['merger_table_marble'](x,y);
    if(x>=4&&x<=11)return textures['merger_table_marble'](x,y);
    return [0,0,0,0];
  },
  'ad_billboard': (x,y)=>{
    if(x<1||x>14||y<3||y>12)return [0,0,0,0];
    return textures['ad_billboard_ad'](x,y);
  },
  'realty_kiosk': (x,y)=>{
    if(y<6||y>14)return [0,0,0,0];
    if(y>=6&&y<=11)return textures['realty_kiosk_map'](x,y);
    return textures['realty_kiosk_wood'](x,y);
  },
  'hr_desk': (x,y)=>{
    if(y<4||y>13)return [0,0,0,0];
    if(y<=8)return textures['hr_desk_wood'](x,y);
    return textures['hr_desk_screen'](x-4,y-9);
  },
  'meta_portal': (x,y)=>{
    if(x<2||x>13)return [0,0,0,0];
    return textures['meta_portal_portal'](x,y);
  },
  'insurance_kiosk': (x,y)=>{
    if(x<2||x>13||y<1||y>14)return [0,0,0,0];
    return textures['insurance_kiosk_metal'](x,y);
  },
};
for(const [name,fn] of Object.entries(itemBase)){
  fs.writeFileSync(path.join(itemDir,name+'.png'),makePNG(16,fn));
  count++;
}

// GUI backgrounds (256x256, only top-left 176x200 visible)
const guiDir='src/main/resources/assets/campuslife/textures/gui';
const guiBgs = {
  'loan': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [60,70,80,255];
    return tint([45,55,65,255],noise(x,y,91)*0.5,0);
  },
  'patent': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [120,90,40,255];
    return tint([220,200,160,255],noise(x,y,92)*0.5,0);
  },
  'merger': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [60,60,80,255];
    return tint([225,225,235,255],noise(x,y,93)*0.3,0);
  },
  'marketing': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [200,60,80,255];
    return tint([60,40,80,255],noise(x,y,94)*0.5,0);
  },
  'realty': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [100,70,40,255];
    return tint([240,220,180,255],noise(x,y,95)*0.3,0);
  },
  'employee': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [80,50,30,255];
    return tint([50,40,30,255],noise(x,y,96)*0.5,0);
  },
  'metaverse': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [120,60,180,255];
    return tint([30,20,60,255],noise(x,y,97)*0.5,0);
  },
  'insurance': (x,y)=>{
    if(x>=176||y>=200)return [0,0,0,0];
    if(x===0||x===175||y===0||y===199)return [50,80,140,255];
    return tint([180,200,220,255],noise(x,y,98)*0.3,0);
  },
};
for(const [name,fn] of Object.entries(guiBgs)){
  fs.writeFileSync(path.join(guiDir,name+'.png'),makePNG(256,fn));
  count++;
}

console.log(`v17 textures: ${count} files generated`);
