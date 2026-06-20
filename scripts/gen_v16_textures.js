// 为 5 个新方块生成纹理
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
    raw[i] = Math.max(0, Math.min(255, r|0));
    raw[i+1] = Math.max(0, Math.min(255, g|0));
    raw[i+2] = Math.max(0, Math.min(255, b|0));
    raw[i+3] = Math.max(0, Math.min(255, a|0));
  }
  const scan = Buffer.alloc((size * 4 + 1) * size);
  for (let y = 0; y < size; y++) {
    scan[y * (size * 4 + 1)] = 0;
    raw.copy(scan, y * (size * 4 + 1) + 1, y * size * 4, (y + 1) * size * 4);
  }
  return Buffer.concat([sig, chunk('IHDR', ihdr), chunk('IDAT', zlib.deflateSync(scan)), chunk('IEND', Buffer.alloc(0))]);
}
function noise(x, y, seed) {
  const n = ((x * 73856093) ^ (y * 19349663) ^ (seed * 83492791)) >>> 0;
  return ((n * 1103515245 + 12345) & 0x7FFFFFFF) / 0x7FFFFFFF;
}
function vary(base, x, y, seed, amount=15) {
  const n = (noise(x, y, seed) - 0.5) * 2 * amount;
  return [base[0]+n, base[1]+n, base[2]+n, base[3]];
}

const blockDir = 'src/main/resources/assets/campuslife/textures/block';
const itemDir = 'src/main/resources/assets/campuslife/textures/item';
const guiDir = 'src/main/resources/assets/campuslife/textures/gui';

const tex = {
  // ---- contract_desk ----
  'contract_desk_top': (x, y) => {
    let c = vary([130, 90, 50, 255], x, y, 40, 15);
    if (y % 4 === 0) c = [c[0]-15, c[1]-10, c[2]-5, 255];
    if (x === 0 || y === 0 || x === 15 || y === 15) c = [c[0]-25, c[1]-20, c[2]-15, 255];
    return c;
  },
  'contract_desk_side': (x, y) => {
    let c = vary([105, 70, 35, 255], x, y, 41, 12);
    if (y % 5 === 0) c = [c[0]-12, c[1]-8, c[2]-4, 255];
    return c;
  },
  'contract_desk_bottom': (x, y) => vary([80, 50, 25, 255], x, y, 42, 8),
  'contract_desk_paper': (x, y) => {
    if (x === 0 || x === 15 || y === 0 || y === 15) return [180, 170, 150, 255];
    let c = [248, 245, 235, 255];
    if (y === 4 && x >= 2 && x <= 13) c = [80, 80, 100, 255];
    if (y === 7 && x >= 2 && x <= 11) c = [80, 80, 100, 255];
    if (y === 10 && x >= 2 && x <= 9) c = [80, 80, 100, 255];
    if (y >= 12 && y <= 13 && x >= 11 && x <= 13) c = [200, 50, 50, 255];
    return c;
  },
  'contract_desk_stamp': (x, y) => {
    const cx = 7.5, cy = 7.5;
    const d = Math.sqrt((x-cx)**2 + (y-cy)**2);
    if (d < 7) return [200, 50, 50, 255];
    if (d < 7.5) return [150, 30, 30, 255];
    return [180, 40, 40, 255];
  },

  // ---- esg_display ----
  'esg_base': (x, y) => {
    let c = vary([60, 70, 80, 255], x, y, 43, 10);
    if (x === 0 || y === 0 || x === 15 || y === 15) c = [40, 50, 60, 255];
    return c;
  },
  'esg_screen': (x, y) => {
    if (x === 0 || x === 15 || y === 0 || y === 15) return [30, 40, 50, 255];
    // 三个柱状图代表 E/S/G
    if (x >= 2 && x <= 4) {
      // E - 绿色
      if (y >= 4 && y <= 14) return [60, 200, 80, 255];
    }
    if (x >= 6 && x <= 8) {
      // S - 蓝色
      if (y >= 6 && y <= 14) return [60, 130, 220, 255];
    }
    if (x >= 10 && x <= 12) {
      // G - 紫色
      if (y >= 8 && y <= 14) return [180, 80, 220, 255];
    }
    // 网格背景
    if ((x+y) % 4 === 0) return [25, 35, 50, 255];
    return [15, 25, 40, 255];
  },
  'esg_frame': (x, y) => vary([90, 100, 110, 255], x, y, 44, 8),

  // ---- ipo_bell ----
  'ipo_base': (x, y) => {
    let c = vary([140, 95, 55, 255], x, y, 45, 18);
    if (y % 4 === 0) c = [c[0]-15, c[1]-10, c[2]-5, 255];
    return c;
  },
  'ipo_wood': (x, y) => {
    let c = vary([110, 70, 35, 255], x, y, 46, 15);
    if (y % 3 === 0) c = [c[0]-12, c[1]-8, c[2]-4, 255];
    return c;
  },
  'ipo_bell': (x, y) => {
    let c = vary([225, 185, 50, 255], x, y, 47, 25);
    if ((x+y) % 3 === 0) c = [c[0]+20, c[1]+15, c[2]+5, 255];
    if (x < 4 && y < 4) c = [c[0]+30, c[1]+25, c[2]+10, 255]; // 高光
    if (x === 0 || x === 15 || y === 0 || y === 15) c = [c[0]-50, c[1]-40, c[2]-20, 255];
    return c;
  },
  'ipo_screen': (x, y) => {
    if (x === 0 || x === 15 || y === 0 || y === 15) return [40, 40, 50, 255];
    // 股价 K 线
    if (y === 7 && x >= 2 && x <= 13) return [80, 220, 80, 255]; // 主线
    if (x === 4 && y >= 4 && y <= 9) return [80, 220, 80, 255]; // 涨柱
    if (x === 8 && y >= 6 && y <= 11) return [220, 80, 80, 255]; // 跌柱
    if (x === 12 && y >= 3 && y <= 8) return [80, 220, 80, 255];
    return [10, 20, 30, 255];
  },

  // ---- vc_table ----
  'vc_table_top': (x, y) => {
    let c = vary([155, 155, 165, 255], x, y, 48, 10);
    if (x === 0 || y === 0 || x === 15 || y === 15) c = [c[0]-30, c[1]-30, c[2]-30, 255];
    if ((x === 7 || x === 8) && y >= 4 && y <= 11) c = [200, 200, 210, 255]; // 中线
    return c;
  },
  'vc_table_side': (x, y) => vary([130, 130, 140, 255], x, y, 49, 12),
  'vc_table_leg': (x, y) => vary([60, 60, 70, 255], x, y, 50, 8),
  'vc_screen': (x, y) => {
    if (x === 0 || x === 15 || y === 0 || y === 15) return [20, 20, 30, 255];
    // 估值数字
    if (y === 4 && x >= 2 && x <= 13) return [220, 200, 60, 255]; // 标题
    if (y === 7 && x >= 2 && x <= 13) return [60, 220, 100, 255]; // 估值
    if (y === 10 && x >= 2 && x <= 13) return [180, 200, 220, 255]; // 轮次
    return [15, 25, 50, 255];
  },

  // ---- stock_ticker ----
  'stock_ticker_frame': (x, y) => vary([50, 50, 60, 255], x, y, 51, 8),
  'stock_ticker_back': (x, y) => vary([30, 30, 40, 255], x, y, 52, 6),
  'stock_ticker_screen': (x, y) => {
    // 滚动股价显示屏
    if (x === 0 || x === 15 || y === 0 || y === 15) return [80, 80, 90, 255];
    // 多行股价
    if (y >= 2 && y <= 4) {
      if (x >= 2 && x <= 5) return [60, 220, 80, 255]; // 涨股
      if (x >= 7 && x <= 10) return [220, 60, 60, 255]; // 跌股
      if (x >= 12 && x <= 14) return [220, 200, 60, 255];
    }
    if (y >= 6 && y <= 8) {
      if (x >= 2 && x <= 6) return [220, 200, 60, 255];
      if (x >= 8 && x <= 13) return [60, 220, 80, 255];
    }
    if (y >= 10 && y <= 12) {
      if (x >= 2 && x <= 8) return [60, 220, 80, 255];
      if (x >= 10 && x <= 14) return [220, 60, 60, 255];
    }
    return [10, 15, 25, 255];
  },
};

let count = 0;
for (const [name, fn] of Object.entries(tex)) {
  fs.writeFileSync(path.join(blockDir, name + '.png'), makePNG(16, fn));
  count++;
}

// 物品贴图(直接用方块的代表色)
const itemTex = {
  'contract_desk': (x, y) => x === 0 || x === 15 || y === 0 || y === 15 ? [80, 50, 25, 255] : [248, 245, 235, 255],
  'esg_display': (x, y) => x === 0 || x === 15 || y === 0 || y === 15 ? [40, 50, 60, 255] : [60, 200, 80, 255],
  'ipo_bell': (x, y) => {
    const cx = 7.5, cy = 7.5;
    const d = Math.sqrt((x-cx)**2 + (y-cy)**2);
    if (d > 7) return [0, 0, 0, 0];
    return [225, 185, 50, 255];
  },
  'vc_table': (x, y) => x === 0 || x === 15 || y === 0 || y === 15 ? [60, 60, 70, 255] : [155, 155, 165, 255],
  'stock_ticker': (x, y) => x === 0 || x === 15 || y === 0 || y === 15 ? [50, 50, 60, 255] : [60, 220, 80, 255],
};

for (const [name, fn] of Object.entries(itemTex)) {
  fs.writeFileSync(path.join(itemDir, name + '.png'), makePNG(16, fn));
  count++;
}

// GUI 背景图(176x200 主面板 + 玩家背包)- 在 256x256 画布
const guiList = ['contract', 'esg', 'ipo', 'vc', 'stock'];
const guiColors = {
  contract: [80, 60, 40], esg: [40, 80, 60], ipo: [80, 70, 40], vc: [50, 50, 70], stock: [40, 60, 80]
};
for (const n of guiList) {
  const [r, g, b] = guiColors[n];
  fs.writeFileSync(path.join(guiDir, n + '.png'), makePNG(256, (x, y) => {
    if (x < 176 && y < 200) {
      if (x === 0 || x === 175 || y === 0 || y === 199) return [r+30, g+30, b+30, 255];
      // 玩家背包格子区域
      if (y >= 117 && y <= 199) {
        if ((x-7) % 18 === 0 || (y-122) % 18 === 0) return [r+15, g+15, b+15, 255];
      }
      return [r+5, g+5, b+5, 240];
    }
    return [0, 0, 0, 0];
  }));
  count++;
}

console.log(count + ' 个新方块/物品/GUI 资产已生成');
