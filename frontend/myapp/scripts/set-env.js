const fs = require('fs');
const path = require('path');

const gatewayUrl = process.env.GATEWAY_API_URL || 'http://localhost:8888';
const content = `window.GATEWAY_API_URL = '${gatewayUrl}';\n`;
const filePath = path.join(__dirname, '..', 'public', 'env.js');
fs.writeFileSync(filePath, content, 'utf8');
console.log(`Generated env.js with GATEWAY_API_URL=${gatewayUrl}`);
