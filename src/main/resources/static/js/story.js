const POLL_INTERVAL_MS = 5000;
const PART_URL = '/api/story/part';
const CONTENT_URL = '/api/story/content';
const VOTE_URL = '/api/story/vote';

let currentPart = -1;
let currentFragmentId = null;
let hasVoted = false;

const welcomeEl = document.getElementById('welcome');
const storyEl = document.getElementById('story');
const progressEl = document.getElementById('progress');
const loadingEl = document.getElementById('loading');
const preparingEl = document.getElementById('preparing');
const waitingEl = document.getElementById('waiting');
const storyTextEl = document.getElementById('story-text');
const voteButtonsEl = document.getElementById('vote-buttons');
const voteBtns = document.querySelectorAll('.vote-btn');

async function fetchPart() {
  try {
    const res = await fetch(PART_URL);
    if (!res.ok) return;
    const { part } = await res.json();
    if (part !== currentPart) {
      currentPart = part;
      hasVoted = false;
      await renderState(part);
    }
  } catch {
    // Network error during rolling upgrade — silently retry on next poll
  }
}

async function renderState(part) {
  if (part === 0) {
    showWelcome();
    return;
  }

  showStoryLoading();

  try {
    const res = await fetch(CONTENT_URL);
    if (!res.ok) return;
    const { totalParts, content, fragmentId } = await res.json();

    currentFragmentId = fragmentId;
    progressEl.textContent = `Part ${part} of ${totalParts}`;

    if (!content) {
      showPreparing();
      return;
    }

    showStoryText(content);
    resetVoteButtons();
  } catch {
    showPreparing();
  }
}

function showWelcome() {
  welcomeEl.classList.remove('hidden');
  storyEl.classList.remove('active');
  progressEl.textContent = '';
}

function showStoryLoading() {
  welcomeEl.classList.add('hidden');
  storyEl.classList.add('active');
  loadingEl.classList.add('active');
  preparingEl.classList.remove('active');
  waitingEl.classList.remove('active');
  storyTextEl.classList.remove('visible');
  voteButtonsEl.classList.remove('active');
}

function showPreparing() {
  loadingEl.classList.remove('active');
  preparingEl.classList.add('active');
  storyTextEl.classList.remove('visible');
  voteButtonsEl.classList.remove('active');
}

function showStoryText(content) {
  loadingEl.classList.remove('active');
  preparingEl.classList.remove('active');
  waitingEl.classList.remove('active');
  storyTextEl.textContent = content;
  // Trigger fade-in on next frame
  requestAnimationFrame(() => storyTextEl.classList.add('visible'));
  if (!hasVoted) {
    voteButtonsEl.classList.add('active');
  }
}

function resetVoteButtons() {
  voteBtns.forEach(btn => {
    btn.disabled = false;
    btn.classList.remove('selected', 'dimmed');
  });
}

voteBtns.forEach(btn => {
  btn.addEventListener('click', async () => {
    if (hasVoted || !currentFragmentId) return;
    hasVoted = true;

    const vote = btn.dataset.vote;

    // Visual feedback immediately
    voteBtns.forEach(b => {
      b.disabled = true;
      b.classList.add(b === btn ? 'selected' : 'dimmed');
    });

    try {
      await fetch(VOTE_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fragmentId: currentFragmentId, vote }),
      });
    } catch {
      // Vote failed silently — it's a demo
    }
  });
});

// Bootstrap on page load
fetchPart();
setInterval(fetchPart, POLL_INTERVAL_MS);
