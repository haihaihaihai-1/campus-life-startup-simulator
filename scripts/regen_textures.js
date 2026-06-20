// 高质量像素纹理生成器 — 替换 v15 的纯色占位符
// 使用伪随机噪声 + 边缘高亮 + 中心亮点制造材质感
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

// 伪随机噪声(确定性,基于x,y种子)
function noise(x, y, seed) {
  const n = ((x * 73856093) ^ (y * 19349663) ^ (seed * 83492791)) >>> 0;
  return ((n * 1103515245 + 12345) & 0x7FFFFFFF) / 0x7FFFFFFF;
}
// 给颜色加变化(主色 + 噪声变化量)
function vary(base, x, y, seed, amount=20) {
  const n = (noise(x, y, seed) - 0.5) * 2 * amount;
  return [base[0]+n, base[1]+n, base[2]+n, base[3]];
}
// 边缘暗化(模拟3D边缘)
function edge(x, y, size, base, darkAmount=40) {
  if (x===0 || y===0 || x===size-1 || y===size-1) return [base[0]-darkAmount, base[1]-darkAmount, base[2]-darkAmount, base[3]];
  return base;
}
// 顶部高光(模拟光从上方照射)
function highlight(x, y, base, lightAmount=15) {
  if (y < 3) return [base[0]+lightAmount, base[1]+lightAmount, base[2]+lightAmount, base[3]];
  return base;
}

const blockDir = 'src/main/resources/assets/campuslife/textures/block';
const itemDir = 'src/main/resources/assets/campuslife/textures/item';

// ============ 方块纹理 ============
const blockTextures = {
  // ---- startup_workbench ----
  'startup_workbench_top': (x, y) => {
    let c = vary([130, 90, 50, 255], x, y, 1, 15);
    // 木纹横线
    if (y === 4 || y === 8 || y === 12) c = [c[0]-20, c[1]-15, c[2]-10, 255];
    // 工具印记
    if (x >= 2 && x <= 4 && y >= 2 && y <= 4) c = [80, 80, 90, 255]; // 螺丝
    return edge(x, y, 16, c, 30);
  },
  'startup_workbench_side': (x, y) => {
    let c = vary([105, 70, 35, 255], x, y, 2, 12);
    if (y === 8) c = [c[0]-15, c[1]-10, c[2]-5, 255];
    return edge(x, y, 16, c, 30);
  },
  'startup_workbench_bottom': (x, y) => vary([80, 50, 25, 255], x, y, 3, 8),
  'startup_workbench_screen': (x, y) => {
    if (x <= 1 || x >= 14 || y <= 1 || y >= 14) return [40, 40, 50, 255]; // 边框
    // 屏幕内容
    if (y >= 3 && y <= 5) return [40, 200, 100, 255]; // 绿色进度条
    if (y >= 7 && y <= 8 && x >= 3 && x <= 12) return [200, 200, 230, 255]; // 文字行
    if (y >= 10 && y <= 11 && x >= 3 && x <= 9) return [200, 200, 230, 255];
    return [30, 60, 110, 255]; // 蓝色背景
  },

  // ---- market_stall ----
  'market_stall_wood': (x, y) => {
    let c = vary([140, 95, 55, 255], x, y, 4, 18);
    // 木纹
    if (y % 4 === 0) c = [c[0]-15, c[1]-10, c[2]-5, 255];
    return edge(x, y, 16, c, 30);
  },
  'market_stall_cloth': (x, y) => {
    // 红白条纹遮阳布
    const stripe = Math.floor(x / 2) % 2;
    let c = stripe ? [220, 60, 60, 255] : [240, 240, 240, 255];
    c = vary(c, x, y, 5, 10);
    return edge(x, y, 16, c, 25);
  },
  'market_stall_fruit': (x, y) => {
    // 多色水果筐
    const cell = (Math.floor(x/4) + Math.floor(y/4)*4);
    const fruits = [
      [220, 60, 60, 255],   // 苹果
      [240, 200, 60, 255],  // 香蕉
      [100, 180, 60, 255],  // 青苹果
      [220, 130, 60, 255],  // 橙子
    ];
    let c = fruits[cell % 4];
    // 中心亮点
    const lx = x % 4, ly = y % 4;
    if (lx === 1 && ly === 1) c = [c[0]+30, c[1]+30, c[2]+30, 255];
    return c;
  },

  // ---- bank_counter ----
  'bank_counter_stone': (x, y) => {
    let c = vary([115, 115, 120, 255], x, y, 6, 20);
    // 石材纹路
    if ((x+y) % 5 === 0) c = [c[0]-15, c[1]-15, c[2]-15, 255];
    return edge(x, y, 16, c, 25);
  },
  'bank_counter_metal': (x, y) => {
    let c = vary([170, 170, 180, 255], x, y, 7, 12);
    return highlight(x, y, edge(x, y, 16, c, 30), 20);
  },
  'bank_counter_screen': (x, y) => {
    if (x <= 1 || x >= 14 || y <= 1 || y >= 14) return [50, 50, 60, 255];
    // 余额数字
    if (y >= 4 && y <= 6 && x >= 3 && x <= 12) return [220, 220, 100, 255]; // 黄色数字
    if (y >= 9 && y <= 10 && x >= 3 && x <= 8) return [100, 220, 100, 255]; // 绿色OK
    return [25, 50, 110, 255];
  },
  'bank_counter_gold': (x, y) => {
    let c = vary([225, 185, 50, 255], x, y, 8, 25);
    // 金色高光
    if ((x+y) % 4 === 0) c = [c[0]+20, c[1]+15, c[2]+5, 255];
    return edge(x, y, 16, c, 40);
  },

  // ---- coffee_machine ----
  'coffee_machine_metal': (x, y) => {
    let c = vary([85, 85, 90, 255], x, y, 9, 12);
    return highlight(x, y, edge(x, y, 16, c, 25), 15);
  },
  'coffee_machine_glass': (x, y) => {
    // 半透明玻璃带反射
    let alpha = 180;
    if (y < 4) alpha = 220; // 顶部更不透明
    let c = [70, 130, 150, alpha];
    if (x === 1 || x === 14) c = [c[0]+30, c[1]+30, c[2]+40, alpha+20]; // 反光
    return c;
  },
  'coffee_machine_display': (x, y) => {
    if (x <= 1 || x >= 14 || y <= 1 || y >= 14) return [30, 30, 40, 255];
    // LED 数字
    if (y >= 5 && y <= 9 && x >= 3 && x <= 12) {
      if ((x === 4 || x === 7 || x === 10) && (y === 6 || y === 8)) return [80, 250, 80, 255];
      if ((y === 5 || y === 7 || y === 9)) return [80, 250, 80, 255];
    }
    return [15, 50, 15, 255];
  },
  'coffee_machine_spout': (x, y) => {
    let c = vary([55, 55, 60, 255], x, y, 10, 8);
    return edge(x, y, 16, c, 25);
  },

  // ---- desk ----
  'desk_wood': (x, y) => {
    let c = vary([135, 90, 45, 255], x, y, 11, 15);
    if (y % 5 === 0) c = [c[0]-12, c[1]-8, c[2]-4, 255];
    return edge(x, y, 16, c, 25);
  },
  'desk_top': (x, y) => {
    let c = vary([155, 105, 60, 255], x, y, 12, 18);
    // 桌面木纹
    if (y % 6 === 2) c = [c[0]-20, c[1]-15, c[2]-8, 255];
    return edge(x, y, 16, c, 20);
  },
  'desk_metal': (x, y) => {
    let c = vary([185, 185, 190, 255], x, y, 13, 10);
    return highlight(x, y, edge(x, y, 16, c, 30), 15);
  },

  // ---- chair ----
  'chair_wood': (x, y) => {
    let c = vary([125, 80, 40, 255], x, y, 14, 14);
    if (y % 4 === 0) c = [c[0]-10, c[1]-8, c[2]-4, 255];
    return edge(x, y, 16, c, 25);
  },
  'chair_seat': (x, y) => {
    let c = vary([105, 65, 30, 255], x, y, 15, 12);
    // 坐垫纹理
    if ((x+y) % 3 === 0) c = [c[0]-8, c[1]-6, c[2]-3, 255];
    return edge(x, y, 16, c, 20);
  },

  // ---- blackboard ----
  'blackboard_board': (x, y) => {
    let c = vary([28, 38, 32, 255], x, y, 16, 8);
    // 粉笔痕迹
    if (x === 3 && y >= 4 && y <= 12) c = [220, 220, 220, 255];
    if (y === 8 && x >= 7 && x <= 12) c = [200, 200, 200, 255];
    if (y === 11 && x >= 6 && x <= 9) c = [180, 180, 180, 255];
    return c;
  },
  'blackboard_frame': (x, y) => {
    let c = vary([85, 55, 25, 255], x, y, 17, 12);
    if (y % 3 === 0) c = [c[0]-10, c[1]-7, c[2]-3, 255];
    return edge(x, y, 16, c, 30);
  },

  // ---- locker ----
  'locker_metal': (x, y) => {
    let c = vary([105, 105, 115, 255], x, y, 18, 12);
    // 储物柜门缝
    if (x === 0 || x === 15 || y === 0 || y === 15) c = [c[0]-30, c[1]-30, c[2]-30, 255];
    if (x === 7) c = [c[0]-15, c[1]-15, c[2]-15, 255]; // 中缝
    // 把手位置
    if (x >= 2 && x <= 4 && y >= 7 && y <= 9) c = [200, 200, 210, 255];
    if (x >= 11 && x <= 13 && y >= 7 && y <= 9) c = [200, 200, 210, 255];
    return c;
  },
  'locker_handle': (x, y) => {
    let c = vary([205, 205, 215, 255], x, y, 19, 8);
    return highlight(x, y, c, 25);
  },
  'locker_top': (x, y) => vary([125, 125, 135, 255], x, y, 20, 12),

  // ---- incubator (带玻璃培养仓+设备感) ----
  'incubator_base': (x, y) => {
    let c = vary([75, 75, 85, 255], x, y, 21, 10);
    // 散热孔
    if (y >= 12 && (x === 3 || x === 6 || x === 9 || x === 12)) c = [30, 30, 40, 255];
    return edge(x, y, 16, c, 25);
  },
  'incubator_glass': (x, y) => {
    let alpha = 140;
    let c = [150, 210, 255, alpha];
    // 反光条
    if (x === 2 || x === 13) c = [c[0]+40, c[1]+30, c[2]+0, alpha+30];
    // 内部脉冲指示灯
    if (x >= 7 && x <= 8 && y >= 7 && y <= 8) c = [100, 255, 200, 220];
    return c;
  },
  'incubator_panel': (x, y) => {
    if (x === 0 || y === 0 || x === 15 || y === 15) return [25, 25, 35, 255];
    // 控制按钮
    if (x === 4 && y === 4) return [60, 220, 60, 255]; // 绿
    if (x === 8 && y === 4) return [220, 60, 60, 255]; // 红
    if (x === 12 && y === 4) return [220, 220, 60, 255]; // 黄
    // LCD 屏
    if (y >= 7 && y <= 11 && x >= 3 && x <= 12) {
      if ((x+y) % 2 === 0) return [80, 200, 220, 255];
      return [40, 100, 130, 255];
    }
    return vary([35, 35, 45, 255], x, y, 22, 8);
  },
  'incubator_top': (x, y) => {
    let c = vary([95, 95, 105, 255], x, y, 23, 10);
    // 顶部通风格栅
    if ((y === 4 || y === 8 || y === 12) && x >= 3 && x <= 12) c = [40, 40, 50, 255];
    return highlight(x, y, edge(x, y, 16, c, 25), 15);
  },

  // ---- auction_block ----
  'auction_block_wood': (x, y) => {
    let c = vary([125, 85, 45, 255], x, y, 24, 16);
    // 雕花
    if ((x === 4 || x === 11) && (y === 4 || y === 11)) c = [c[0]-30, c[1]-20, c[2]-10, 255];
    if (y % 5 === 0) c = [c[0]-10, c[1]-7, c[2]-3, 255];
    return edge(x, y, 16, c, 30);
  },
  'auction_block_cloth': (x, y) => {
    let c = vary([65, 45, 125, 255], x, y, 25, 12);
    // 紫色丝绒纹理
    if ((x+y) % 4 === 0) c = [c[0]+15, c[1]+10, c[2]+30, 255];
    return edge(x, y, 16, c, 25);
  },
  'auction_block_gold': (x, y) => {
    let c = vary([225, 185, 50, 255], x, y, 26, 25);
    // 金色饰条
    if (y === 7 || y === 8) c = [c[0]+20, c[1]+15, c[2]+5, 255];
    if ((x+y) % 3 === 0) c = [c[0]+15, c[1]+10, c[2]-5, 255];
    return edge(x, y, 16, c, 35);
  },

  // ---- crypto_miner ----
  'crypto_miner_case': (x, y) => {
    let c = vary([45, 45, 55, 255], x, y, 27, 8);
    // 散热鳍片
    if (y % 2 === 0) c = [c[0]-10, c[1]-10, c[2]-10, 255];
    return edge(x, y, 16, c, 20);
  },
  'crypto_miner_fan': (x, y) => {
    const cx = 7.5, cy = 7.5;
    const dx = x - cx, dy = y - cy;
    const d = Math.sqrt(dx*dx + dy*dy);
    if (d > 7) return [40, 40, 50, 255]; // 边框
    if (d > 6.5) return [80, 80, 90, 255]; // 风扇圈
    // 风扇叶片(三叶)
    const angle = Math.atan2(dy, dx) + Math.PI;
    const blade = Math.floor((angle / (Math.PI * 2)) * 3);
    const offset = ((angle / (Math.PI * 2)) * 3) - blade;
    if (offset < 0.5 && d > 1) return [70, 70, 80, 255];
    return [50, 50, 60, 255];
  },
  'crypto_miner_led': (x, y) => {
    let c = vary([45, 45, 55, 255], x, y, 28, 6);
    // 排 LED 灯
    if (y === 3 || y === 7 || y === 11) {
      if (x % 3 === 1) {
        // 不同状态颜色
        const stat = Math.floor(y/4);
        if (stat === 0) c = [40, 240, 60, 255];   // 绿
        else if (stat === 1) c = [240, 200, 40, 255]; // 黄
        else c = [240, 80, 40, 255];                  // 红
      }
    }
    return edge(x, y, 16, c, 20);
  },
  'crypto_miner_top': (x, y) => {
    let c = vary([55, 55, 65, 255], x, y, 29, 10);
    // 顶部通风
    if ((x === 3 || x === 8 || x === 13) && y >= 3 && y <= 12) c = [25, 25, 35, 255];
    return edge(x, y, 16, c, 20);
  },
};

let blockCount = 0;
for (const [name, fn] of Object.entries(blockTextures)) {
  fs.writeFileSync(path.join(blockDir, name + '.png'), makePNG(16, fn));
  blockCount++;
}

// ============ 物品纹理(更精致) ============
const itemTextures = {
  'coin': (x, y) => {
    const cx = 7.5, cy = 7.5;
    const d = Math.sqrt((x-cx)**2 + (y-cy)**2);
    if (d > 7) return [0, 0, 0, 0];
    if (d > 6) return [180, 140, 30, 255]; // 外圈
    if (d > 5.5) return [220, 180, 40, 255]; // 边
    // 中心¥字
    if ((x === 7 || x === 8) && y >= 5 && y <= 10) return [180, 140, 30, 255];
    if ((y === 6 || y === 8) && x >= 5 && x <= 10) return [180, 140, 30, 255];
    let c = [240, 200, 60, 255];
    // 高光
    if (x < cx && y < cy && d < 4) c = [c[0]+20, c[1]+20, c[2]+0, 255];
    return c;
  },
  'business_plan': (x, y) => {
    if (x < 2 || x > 13 || y < 2 || y > 14) return [0, 0, 0, 0];
    if (x === 2 || x === 13 || y === 2 || y === 14) return [80, 60, 40, 255];
    // 标题栏
    if (y === 4) return [60, 80, 180, 255];
    if (y === 5) return [50, 70, 160, 255];
    // 文字行
    if (y === 7 && x >= 4 && x <= 11) return [80, 80, 80, 255];
    if (y === 9 && x >= 4 && x <= 9) return [80, 80, 80, 255];
    if (y === 11 && x >= 4 && x <= 12) return [80, 80, 80, 255];
    // 印章
    if (x >= 9 && x <= 12 && y === 13) return [200, 50, 50, 255];
    return [248, 245, 230, 255];
  },
  'innovation_chip': (x, y) => {
    // 主体
    if (x >= 4 && x <= 11 && y >= 4 && y <= 11) {
      if (x === 4 || x === 11 || y === 4 || y === 11) return [40, 40, 50, 255];
      // 电路图案
      if ((x === 6 || x === 9) && y >= 5 && y <= 10) return [80, 200, 80, 255];
      if (y === 7 && x >= 5 && x <= 10) return [80, 200, 80, 255];
      return [40, 70, 40, 255];
    }
    // 引脚
    if ((y === 2 || y === 13) && x >= 5 && x <= 10 && x % 2 === 1) return [200, 200, 100, 255];
    if ((x === 2 || x === 13) && y >= 5 && y <= 10 && y % 2 === 1) return [200, 200, 100, 255];
    return [0, 0, 0, 0];
  },
  'startup_kit': (x, y) => {
    if (x < 1 || x > 14 || y < 4 || y > 14) return [0, 0, 0, 0];
    // 把手
    if (y === 3 && x >= 6 && x <= 9) return [80, 50, 20, 255];
    if (y === 4 && (x === 6 || x === 9)) return [80, 50, 20, 255];
    // 箱体
    if (y === 4 || y === 14 || x === 1 || x === 14) return [80, 50, 20, 255];
    // 锁扣
    if (y === 8 && x >= 7 && x <= 8) return [200, 180, 60, 255];
    let c = [145, 95, 45, 255];
    // 木纹
    if (y === 7 || y === 11) c = [c[0]-15, c[1]-10, c[2]-5, 255];
    return c;
  },
  'tech_gadget': (x, y) => {
    const cx = 7.5, cy = 7.5;
    const d = Math.sqrt((x-cx)**2 + (y-cy)**2);
    if (d > 7) return [0, 0, 0, 0];
    if (d > 6) return [40, 40, 50, 255]; // 外壳
    if (d > 5) return [180, 180, 200, 255]; // 边
    // 中心屏
    if (d < 3) {
      if ((x+y) % 2 === 0) return [40, 220, 220, 255];
      return [40, 180, 200, 255];
    }
    // 按钮
    if ((y === 5 || y === 10) && (x === 5 || x === 10)) return [220, 60, 60, 255];
    return [60, 60, 75, 255];
  },
  'textbook': (x, y) => {
    if (x < 1 || x > 14 || y < 1 || y > 15) return [0, 0, 0, 0];
    // 书脊
    if (x === 1 || x === 2) return [180, 40, 40, 255];
    if (x === 3) return [140, 30, 30, 255];
    // 边
    if (x === 14 || y === 1 || y === 15) return [120, 30, 30, 255];
    // 标题
    if (y >= 4 && y <= 5 && x >= 5 && x <= 11) return [120, 30, 30, 255];
    // 装饰线
    if (y === 8 && x >= 5 && x <= 11) return [180, 40, 40, 255];
    let c = [225, 205, 175, 255];
    // 旧纸效果
    if ((x+y) % 7 === 0) c = [c[0]-15, c[1]-15, c[2]-15, 255];
    return c;
  },
  'pencil': (x, y) => {
    // 斜放铅笔
    const dy = x - y;
    if (dy < -2 || dy > 2) return [0, 0, 0, 0];
    if (x + y < 4 || x + y > 27) return [0, 0, 0, 0];
    // 笔尖(右上)
    if (x + y > 23) {
      if (dy === 0) return [40, 40, 40, 255]; // 笔芯
      return [220, 180, 100, 255]; // 木头
    }
    // 笔身
    if (x + y > 9) {
      if (dy === 0) return [220, 200, 50, 255]; // 中线
      return [200, 170, 40, 255];
    }
    // 橡皮(左下)
    return [220, 80, 80, 255];
  },
  'eraser': (x, y) => {
    if (x < 3 || x > 12 || y < 3 || y > 12) return [0, 0, 0, 0];
    if (x === 3 || x === 12 || y === 3 || y === 12) return [200, 100, 100, 255];
    let c = [240, 130, 130, 255];
    // 商标条
    if (y >= 6 && y <= 8) c = [200, 100, 100, 255];
    return c;
  },
  'ruler': (x, y) => {
    if (y < 5 || y > 11) return [0, 0, 0, 0];
    // 透明感
    let c = [200, 220, 240, 240];
    if (y === 5 || y === 11) c = [150, 170, 200, 255];
    // 刻度
    if (y === 6 || y === 10) {
      if (x % 2 === 0) c = [80, 100, 130, 255];
    }
    if (y === 7 && x % 4 === 0) c = [80, 100, 130, 255]; // 长刻度
    return c;
  },
  'coffee_cup': (x, y) => {
    const cx = 6;
    // 杯把
    if (x >= cx+4 && x <= cx+7 && y >= 7 && y <= 11) {
      if (x === cx+7 || y === 7 || y === 11) return [200, 200, 200, 255];
      if (x === cx+4) return [0, 0, 0, 0];
      return [0, 0, 0, 0];
    }
    // 杯子
    if (x >= cx-4 && x <= cx+4 && y >= 4 && y <= 14) {
      if (x === cx-4 || x === cx+4) return [200, 200, 200, 255]; // 杯壁
      if (y === 14) return [180, 180, 180, 255]; // 底
      // 咖啡液面
      if (y === 4) return [220, 220, 220, 255]; // 杯口
      if (y === 5) return [80, 50, 30, 255]; // 咖啡顶
      if (y === 6) return [60, 35, 20, 255]; // 咖啡内
      if (y >= 7 && y <= 13) return [240, 240, 240, 255]; // 杯内白
    }
    return [0, 0, 0, 0];
  },
  'cafeteria_bread': (x, y) => {
    const cx = 7.5, cy = 9;
    const dx = (x - cx) / 6, dy = (y - cy) / 5;
    const d = dx*dx + dy*dy;
    if (d > 1) return [0, 0, 0, 0];
    let c = [220, 170, 100, 255];
    // 顶部高光
    if (y < cy - 2) c = [c[0]+15, c[1]+15, c[2]+8, 255];
    // 底部阴影
    if (y > cy + 2) c = [c[0]-25, c[1]-20, c[2]-12, 255];
    // 芝麻
    if ((x === 5 && y === 6) || (x === 9 && y === 7) || (x === 7 && y === 5)) c = [40, 30, 20, 255];
    return c;
  },
  'milk_carton': (x, y) => {
    if (x < 3 || x > 12 || y < 2 || y > 14) return [0, 0, 0, 0];
    // 顶盖
    if (y === 2) return [240, 240, 240, 255];
    if (y >= 3 && y <= 4) return [80, 130, 180, 255]; // 蓝盖
    if (y === 5) return [60, 110, 160, 255];
    // 标签
    if (y >= 8 && y <= 10 && x >= 5 && x <= 10) {
      if (y === 9 && (x === 6 || x === 9)) return [40, 80, 130, 255];
      return [180, 200, 220, 255];
    }
    // 边
    if (x === 3 || x === 12 || y === 14) return [200, 200, 200, 255];
    return [248, 248, 248, 255];
  },
  'school_lunch': (x, y) => {
    if (x < 1 || x > 14 || y < 4 || y > 14) return [0, 0, 0, 0];
    // 餐盘边
    if (x === 1 || x === 14 || y === 4 || y === 14) return [150, 150, 150, 255];
    // 三格
    if (x <= 6) {
      // 主菜
      if ((x+y) % 3 === 0) return [200, 70, 50, 255];
      return [180, 80, 60, 255];
    }
    if (x <= 10) {
      // 米饭
      if ((x+y) % 2 === 0) return [240, 240, 220, 255];
      return [220, 220, 200, 255];
    }
    // 蔬菜
    if ((x+y) % 3 === 0) return [80, 180, 70, 255];
    return [100, 180, 80, 255];
  },
  'school_badge': (x, y) => {
    const cx = 7.5, cy = 7.5;
    const d = Math.sqrt((x-cx)**2 + (y-cy)**2);
    if (d > 7) return [0, 0, 0, 0];
    if (d > 6) return [120, 30, 30, 255];
    if (d > 5.5) return [200, 50, 50, 255];
    // 内圈
    if (d > 4) return [180, 40, 40, 255];
    // 中心字
    if ((x === 7 || x === 8) && y >= 5 && y <= 10) return [220, 200, 60, 255];
    if ((y === 6 || y === 9) && x >= 5 && x <= 10) return [220, 200, 60, 255];
    return [200, 50, 50, 255];
  },
  'exam_paper': (x, y) => {
    if (x < 2 || x > 13 || y < 1 || y > 14) return [0, 0, 0, 0];
    if (x === 2 || x === 13 || y === 1 || y === 14) return [180, 180, 180, 255];
    // 标题
    if (y >= 3 && y <= 4 && x >= 4 && x <= 11) return [60, 60, 60, 255];
    // 题目行
    if (y === 6 && x >= 3 && x <= 12) return [60, 60, 60, 255];
    if (y === 9 && x >= 3 && x <= 10) return [60, 60, 60, 255];
    if (y === 12 && x >= 3 && x <= 11) return [60, 60, 60, 255];
    // 红笔批改
    if (y === 5 && x === 12) return [220, 50, 50, 255];
    return [250, 248, 240, 255];
  },
  'report_card': (x, y) => {
    if (x < 1 || x > 14 || y < 1 || y > 15) return [0, 0, 0, 0];
    if (x === 1 || x === 14 || y === 1 || y === 15) return [180, 140, 30, 255];
    // 顶部金条
    if (y >= 2 && y <= 4) return [220, 180, 60, 255];
    // 标题
    if (y >= 6 && y <= 7 && x >= 4 && x <= 11) return [180, 140, 30, 255];
    // 数据行
    if ((y === 9 || y === 11 || y === 13) && x >= 3 && x <= 12) return [80, 80, 80, 255];
    return [248, 240, 220, 255];
  },
  'handmade_craft': (x, y) => {
    if (x < 2 || x > 13 || y < 2 || y > 13) return [0, 0, 0, 0];
    // 缠绕的麻绳
    const c = (x + y) % 4;
    if (c === 0) return [180, 100, 50, 255];
    if (c === 1) return [140, 80, 40, 255];
    if (c === 2) return [200, 130, 70, 255];
    return [160, 90, 45, 255];
  },
  'raw_material': (x, y) => {
    if (x < 2 || x > 13 || y < 4 || y > 13) return [0, 0, 0, 0];
    let c = vary([130, 110, 85, 255], x, y, 30, 18);
    // 边
    if (x === 2 || x === 13 || y === 4 || y === 13) c = [c[0]-30, c[1]-25, c[2]-20, 255];
    // 麻袋纹理
    if ((x+y) % 3 === 0) c = [c[0]-10, c[1]-8, c[2]-6, 255];
    return c;
  },
  'incubator': (x, y) => {
    // 立体孵化器图标
    if (x < 2 || x > 13 || y < 1 || y > 14) return [0, 0, 0, 0];
    // 顶盖
    if (y <= 2) return [85, 85, 95, 255];
    // 玻璃仓
    if (y >= 3 && y <= 11) {
      if (x === 2 || x === 13) return [70, 70, 80, 255];
      if ((x === 7 || x === 8) && (y === 6 || y === 7)) return [100, 255, 150, 255]; // 内部生物
      return [150, 210, 255, 200];
    }
    // 底座
    if (y >= 12) {
      if (x === 4 && y === 13) return [60, 220, 60, 255]; // LED
      if (x === 11 && y === 13) return [220, 60, 60, 255];
      return [55, 55, 65, 255];
    }
    return [0, 0, 0, 0];
  },
  'auction_block': (x, y) => {
    // 拍卖锤
    if (y === 3 && x >= 6 && x <= 11) return [120, 80, 40, 255]; // 锤头顶
    if (y >= 4 && y <= 6 && x >= 5 && x <= 12) {
      if (x === 5 || x === 12 || y === 4 || y === 6) return [80, 50, 20, 255];
      return [140, 95, 50, 255]; // 锤头
    }
    // 锤柄
    if (x >= 8 && x <= 9 && y >= 7 && y <= 14) return [100, 65, 30, 255];
    return [0, 0, 0, 0];
  },
  'crypto_miner': (x, y) => {
    if (x < 1 || x > 14 || y < 3 || y > 14) return [0, 0, 0, 0];
    // 边
    if (x === 1 || x === 14 || y === 3 || y === 14) return [25, 25, 35, 255];
    // 风扇网格
    if (y >= 5 && y <= 11 && x >= 4 && x <= 11) {
      const cx = 7.5, cy = 8;
      const d = Math.sqrt((x-cx)**2 + (y-cy)**2);
      if (d > 3.5) return [50, 50, 60, 255];
      if (d > 3) return [80, 80, 90, 255];
      return [40, 40, 50, 255];
    }
    // LED
    if (y === 4 && x === 3) return [60, 240, 60, 255];
    if (y === 4 && x === 5) return [240, 60, 60, 255];
    // 通风
    if (y === 13 && (x === 3 || x === 6 || x === 9 || x === 12)) return [25, 25, 35, 255];
    return [55, 55, 65, 255];
  },
};

let itemCount = 0;
for (const [name, fn] of Object.entries(itemTextures)) {
  fs.writeFileSync(path.join(itemDir, name + '.png'), makePNG(16, fn));
  itemCount++;
}

console.log('=== 纹理重生成完成 ===');
console.log(blockCount + ' 张方块纹理');
console.log(itemCount + ' 张物品纹理');
console.log('总计 ' + (blockCount + itemCount) + ' 张');
