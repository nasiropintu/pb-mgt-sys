'use strict';

const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('node:path');
const fs = require('node:fs');
const workerServer = require('./worker-server.cjs');

const DATA_DIR = path.join(app.getPath('userData'), 'pblos-worker');
const SERVER_PORT = 7474;

let mainWindow = null;
let serverUrl = null;

function createWindow() {
  mainWindow = new BrowserWindow({
    title: 'P&B MGT Sys — AI Worker',
    width: 1200,
    height: 800,
    minWidth: 800,
    minHeight: 560,
    backgroundColor: '#f5f7f4',
    webPreferences: {
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: false,
      preload: path.join(__dirname, 'preload.cjs'),
    },
  });

  if (!app.isPackaged) {
    mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL || 'http://127.0.0.1:5173');
  } else {
    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'));
  }
}

function pushStatus() {
  if (!mainWindow || mainWindow.isDestroyed()) return;
  const status = workerServer.getStatus();
  mainWindow.webContents.send('worker-status', { ...status, serverUrl, port: SERVER_PORT });
}

app.whenReady().then(() => {
  fs.mkdirSync(DATA_DIR, { recursive: true });

  const { url } = workerServer.start({
    dataDir: DATA_DIR,
    port: SERVER_PORT,
    onStatusChange: pushStatus,
  });
  serverUrl = url;

  createWindow();
  setTimeout(pushStatus, 1500); // push initial status after UI loads

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    workerServer.stop();
    app.quit();
  }
});

// ── IPC handlers ──────────────────────────────────────────────────────────────

ipcMain.handle('get-status', async () => {
  await workerServer.refreshAiStatus();
  const status = workerServer.getStatus();
  return { ...status, serverUrl, port: SERVER_PORT };
});

ipcMain.handle('get-jobs', async () => {
  const res = await fetch(`http://127.0.0.1:${SERVER_PORT}/api/jobs`);
  return res.json();
});

ipcMain.handle('retry-job', async (_event, jobId) => {
  const res = await fetch(`http://127.0.0.1:${SERVER_PORT}/api/jobs/${jobId}/retry`, { method: 'POST' });
  return res.json();
});

ipcMain.handle('cancel-job', async (_event, jobId) => {
  const res = await fetch(`http://127.0.0.1:${SERVER_PORT}/api/jobs/${jobId}`, { method: 'DELETE' });
  return res.json();
});

ipcMain.handle('refresh-ai', async () => {
  await workerServer.refreshAiStatus();
  pushStatus();
  return workerServer.getStatus();
});
