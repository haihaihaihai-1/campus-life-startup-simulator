// v19 textures generator
const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

function crc32(buf) {
  let c = 0xFFFFFFFF, t = [];
  for (let i = 0; i < 256; i++) { let k = i; for (let j = 0; j < 8; j++) k = (k & 1) ? 0xEDB88320 ^ (k >>> 1) : k >>> 1; t[i] = k; }
  for (let i = 0; i < buf.length; i++) c = t[(c ^ buf[i]) & 0xFF] ^ (c >>> 8);
  return (c ^ 0xFFFFFFFF) >>> 0;
}
function chunk(type, data) {
  const len = Buffer.alloc(4); len.writeUInt32BE(data.length);
  const t = Buffer.from(type); const cd = Buffer.concat([t, data]);
  const c = Buffer.alloc(4); c.writeUInt32BE(crc32(cd));
  return Buffer.concat([len, t, data, c]);
}
function makePNG(size, fn) {
  const sig = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(size, 0); ihdr.writeUInt32BE(size, 4);
  ihdr[8] = 8; ihdr[9] = 6;
  const raw = Buffer.alloc(size * size * 4);
  for (let y = 0; y < size; y++) for (let x = 0; x < size; x++) {
    const [r, g, b, a] = fn(x, y); const i = (y * size + x) * 4;
    raw[i] = r; raw[i+1] = g; raw[i+2] = b; raw[i+3] = a;
  }
  const scan = Buffer.alloc((size * 4 + 1) * size);
  for (let y = 0; y < size; y++) { scan[y * (size * 4 + 1)] = 0; raw.copy(scan, y * (size * 4 + 1) + 1, y * size * 4, (y + 1) * size * 4); }
  return Buffer.concat([sig, chunk('IHDR', ihdr), chunk('IDAT', zlib.deflateSync(scan)), chunk('IEND', Buffer.alloc(0))]);
}

function hash(x, y, seed) { let h = x * 374761393 + y * 668265263 + seed * 2147483647; h = (h ^ (h >>> 13)) * 1274126177; return (h ^ (h >>> 16)) & 0xFF; }
function noise(x, y, seed, range) { return ((hash(x, y, seed) / 255) - 0.5) * range; }
function edge(x, y, base) { if (x === 0 || x === 15 || y === 0 || y === 15) return base * 0.7; return base; }
function high(x, y, base) { if (y < 2) return Math.min(255, base * 1.2); return base; }

const blockDir = 'src/main/resources/assets/campuslife/textures/block';
const itemDir = 'src/main/resources/assets/campuslife/textures/item';
const guiDir = 'src/main/resources/assets/campuslife/textures/gui';
[blockDir, itemDir, guiDir].forEach(d => fs.mkdirSync(d, {recursive:true}));

// Block textures
const blocks = {
  // investor_office (oak wood + brown leather + green screen)
  'investor_office_desk': (x,y) => { const n=noise(x,y,1,30); return [edge(x,y,high(x,y,120))+n, 80+n, 40+n, 255]; },
  'investor_office_top': (x,y) => { const n=noise(x,y,2,20); return [140+n, 100+n, 60+n, 255]; },
  'investor_office_leather': (x,y) => { const n=noise(x,y,3,20); return [90+n, 50+n, 30+n, 255]; },
  'investor_office_screen': (x,y) => { const n=noise(x,y,4,15); if((x+y)%3===0) return [40+n, 200+n, 80+n, 255]; return [30+n, 80+n, 50+n, 255]; },
  // grant_desk (light wood + paper + red seal)
  'grant_desk_wood': (x,y) => { const n=noise(x,y,5,25); return [edge(x,y,180)+n, 140+n, 80+n, 255]; },
  'grant_desk_top': (x,y) => { const n=noise(x,y,6,15); return [200+n, 160+n, 100+n, 255]; },
  'grant_desk_paper': (x,y) => { const n=noise(x,y,7,10); return [240+n, 235+n, 220+n, 255]; },
  'grant_desk_seal': (x,y) => { const n=noise(x,y,8,20); return [200+n, 40+n, 40+n, 255]; },
  // crm_terminal (dark gray case + cyan screen)
  'crm_terminal_case': (x,y) => { const n=noise(x,y,9,15); return [edge(x,y,60)+n, 60+n, 70+n, 255]; },
  'crm_terminal_screen': (x,y) => { const n=noise(x,y,10,20); if(y%3===0) return [40+n, 180+n, 220+n, 255]; return [20+n, 80+n, 120+n, 255]; },
  'crm_terminal_kb': (x,y) => { const n=noise(x,y,11,10); if((x+y)%2===0) return [40+n, 40+n, 50+n, 255]; return [80+n, 80+n, 90+n, 255]; },
  // competition_stage (red carpet + gold step + blue podium)
  'competition_stage_floor': (x,y) => { const n=noise(x,y,12,30); return [edge(x,y,180)+n, 30+n, 40+n, 255]; },
  'competition_stage_step': (x,y) => { const n=noise(x,y,13,20); return [220+n, 180+n, 60+n, 255]; },
  'competition_stage_podium': (x,y) => { const n=noise(x,y,14,15); return [40+n, 60+n, 140+n, 255]; },
  // research_lab (steel + glass tubes)
  'research_lab_side': (x,y) => { const n=noise(x,y,15,15); return [edge(x,y,130)+n, 130+n, 140+n, 255]; },
  'research_lab_top': (x,y) => { const n=noise(x,y,16,15); return [high(x,y,160)+n, 160+n, 170+n, 255]; },
  'research_lab_tube': (x,y) => { const n=noise(x,y,17,20); return [80+n, 200+n, 220+n, 220]; },
  // lab_bench (white + green glass)
  'lab_bench_side': (x,y) => { const n=noise(x,y,18,10); return [edge(x,y,220)+n, 220+n, 230+n, 255]; },
  'lab_bench_top': (x,y) => { const n=noise(x,y,19,10); return [240+n, 240+n, 250+n, 255]; },
  'lab_bench_glass': (x,y) => { const n=noise(x,y,20,15); return [60+n, 200+n, 100+n, 220]; },
  // mentor_chair (dark wood + brown leather)
  'mentor_chair_wood': (x,y) => { const n=noise(x,y,21,20); return [edge(x,y,90)+n, 60+n, 30+n, 255]; },
  'mentor_chair_leather': (x,y) => { const n=noise(x,y,22,20); return [120+n, 70+n, 40+n, 255]; },
  // trophy_case (mahogany + glass + gold)
  'trophy_case_wood': (x,y) => { const n=noise(x,y,23,20); return [edge(x,y,110)+n, 55+n, 30+n, 255]; },
  'trophy_case_glass': (x,y) => { const n=noise(x,y,24,10); return [180+n, 220+n, 240+n, 100]; },
  'trophy_case_gold': (x,y) => { const n=noise(x,y,25,25); return [240+n, 200+n, 60+n, 255]; },
};

let bc = 0;
for (const [name, fn] of Object.entries(blocks)) {
  fs.writeFileSync(path.join(blockDir, name + '.png'), makePNG(16, fn));
  bc++;
}

// Item textures (block icons for the 8 new blocks)
const items = {
  'investor_office': (x,y) => { if(y<4) return [40, 200, 80, 255]; if(y<8) return [90,50,30,255]; return [120,80,40,255]; },
  'grant_desk': (x,y) => { if(y<10) return [240,235,220,255]; if(y<13) return [200,40,40,255]; return [180,140,80,255]; },
  'crm_terminal': (x,y) => { if(y<10) return (y%2===0)?[40,180,220,255]:[20,80,120,255]; return [60,60,70,255]; },
  'competition_stage': (x,y) => { if(y<6) return [40,60,140,255]; if(y<12) return [220,180,60,255]; return [180,30,40,255]; },
  'research_lab': (x,y) => { if(y<8 && (x===4||x===11)) return [80,200,220,255]; return [130,130,140,255]; },
  'lab_bench': (x,y) => { if(y<10 && (x===4||x===11)) return [60,200,100,255]; return [220,220,230,255]; },
  'mentor_chair': (x,y) => { if(y<10) return [120,70,40,255]; return [90,60,30,255]; },
  'trophy_case': (x,y) => { if(x>=7 && x<=9 && y>=4 && y<=14) return [240,200,60,255]; return [110,55,30,255]; },
};
let ic = 0;
for (const [name, fn] of Object.entries(items)) {
  fs.writeFileSync(path.join(itemDir, name + '.png'), makePNG(16, fn));
  ic++;
}

// GUI backgrounds (each 256x256 with themed color)
const guis = {
  'investor': [40, 60, 30],
  'grant': [60, 50, 30],
  'crm': [20, 40, 60],
  'competition': [60, 30, 40],
  'research': [30, 50, 60],
  'lab': [40, 50, 40],
  'mentor': [50, 30, 20],
  'trophy': [60, 50, 20],
};
let gc = 0;
for (const [name, base] of Object.entries(guis)) {
  fs.writeFileSync(path.join(guiDir, name + '.png'), makePNG(256, (x,y) => {
    if (x < 176 && y < 200) {
      if (x === 0 || x === 175 || y === 0 || y === 199) return [base[0]+30, base[1]+30, base[2]+30, 255];
      return [base[0], base[1], base[2], 255];
    }
    return [0, 0, 0, 0];
  }));
  gc++;
}

console.log('v19 textures: ' + bc + ' blocks + ' + ic + ' items + ' + gc + ' GUIs');
