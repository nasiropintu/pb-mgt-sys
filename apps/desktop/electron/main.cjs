const { app, BrowserWindow } = require('electron');
const path = require('node:path');
function createWindow() {
  const window = new BrowserWindow({ title: 'P&B MGT Sys — Private life & work organizer', width: 1180, height: 780, minWidth: 760, minHeight: 560, backgroundColor: '#f5f7f4', webPreferences: { contextIsolation: true, nodeIntegration: false, sandbox: true } });
  if (!app.isPackaged) window.loadURL(process.env.VITE_DEV_SERVER_URL || 'http://127.0.0.1:5173');
  else window.loadFile(path.join(__dirname, '../dist/index.html'));
}
app.whenReady().then(() => { createWindow(); app.on('activate', () => { if (BrowserWindow.getAllWindows().length === 0) createWindow(); }); });
app.on('window-all-closed', () => { if (process.platform !== 'darwin') app.quit(); });
