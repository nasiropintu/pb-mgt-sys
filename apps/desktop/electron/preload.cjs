'use strict';
const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('pblosWorker', {
  getStatus:  ()        => ipcRenderer.invoke('get-status'),
  getJobs:    ()        => ipcRenderer.invoke('get-jobs'),
  retryJob:   (jobId)   => ipcRenderer.invoke('retry-job', jobId),
  cancelJob:  (jobId)   => ipcRenderer.invoke('cancel-job', jobId),
  refreshAi:  ()        => ipcRenderer.invoke('refresh-ai'),
  onStatus:   (cb)      => { ipcRenderer.on('worker-status', (_e, data) => cb(data)); },
});
