const POLL_INTERVAL_MS = 5000;
const POLL_INTERVAL_GENERATING_MS = 1000;

const partCounter = document.getElementById('part-counter');
const advanceBtn = document.getElementById('advance-btn');
const resetBtn = document.getElementById('reset-btn');
const generateBtn = document.getElementById('generate-btn');
const statusBadge = document.getElementById('status-badge');
const deleteBtn = document.getElementById('delete-btn');

let pollTimer = null;
let generating = false;

function updateUI(status) {
  partCounter.textContent = `${status.currentPart} / ${status.totalParts}`;
  advanceBtn.disabled = status.currentPart >= status.totalParts;

  if (status.ready) {
    statusBadge.textContent = 'Ready';
    statusBadge.className = 'badge badge-success';
    generating = false;
    generateBtn.disabled = true;
    generateBtn.textContent = 'Generate Stories';
    // All stories are ready — no need to keep polling until something changes
    clearInterval(pollTimer);
  } else {
    statusBadge.textContent = `${status.storyCount} / ${status.requiredCount} stories`;
    statusBadge.className = 'badge badge-neutral';
    if (!generating) {
      generateBtn.disabled = false;
      generateBtn.textContent = 'Generate Stories';
    }
  }
}

async function fetchStatus() {
  try {
    const res = await fetch('/admin/api/status');
    if (!res.ok) return;
    updateUI(await res.json());
  } catch {
    // Network error — retry on next tick
  }
}

function startPolling(interval) {
  clearInterval(pollTimer);
  pollTimer = setInterval(fetchStatus, interval);
}

advanceBtn.addEventListener('click', async () => {
  advanceBtn.disabled = true;
  try {
    const res = await fetch('/admin/api/advance', { method: 'POST' });
    if (res.ok) updateUI(await res.json());
  } catch {
    // Ignore — next poll will refresh state
  } finally {
    await fetchStatus();
  }
});

resetBtn.addEventListener('click', async () => {
  try {
    const res = await fetch('/admin/api/reset', { method: 'POST' });
    if (res.ok) updateUI(await res.json());
  } catch {
    // Ignore
  }
});

generateBtn.addEventListener('click', async () => {
  generating = true;
  generateBtn.disabled = true;
  generateBtn.textContent = 'Generating…';
  startPolling(POLL_INTERVAL_GENERATING_MS);
  try {
    await fetch('/admin/api/generate', { method: 'POST' });
    // 202 Accepted — generation runs in background, polling will detect completion
  } catch {
    generating = false;
    generateBtn.textContent = 'Generate Stories';
    generateBtn.disabled = false;
    startPolling(POLL_INTERVAL_MS);
  }
});

deleteBtn.addEventListener('click', async () => {
  try {
    const res = await fetch('/admin/api/delete-stories', { method: 'POST' });
    if (res.ok) updateUI(await res.json());
    startPolling(POLL_INTERVAL_MS);
  } catch {
    // Ignore
  }
});

// Bootstrap
fetchStatus();
startPolling(POLL_INTERVAL_MS);
