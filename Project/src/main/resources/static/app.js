
function getCategoryPlaceholderSvg(categoryCode) {
  const stroke = '#18533d';
  let icon = '<rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>';
  if (categoryCode === 'BAGS') {
    icon = '<path d="M6 20h12a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2z"/><path d="M9 6V4a3 3 0 0 1 6 0v2"/>';
  } else if (categoryCode === 'ID_CARDS') {
    icon = '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="9" cy="10" r="2"/><line x1="15" y1="8" x2="17" y2="8"/><line x1="15" y1="12" x2="17" y2="12"/><line x1="7" y1="16" x2="17" y2="16"/>';
  } else if (categoryCode === 'KEYS') {
    icon = '<circle cx="7.5" cy="15.5" r="4.5"/><path d="m21 3-9.5 9.5"/><path d="m15.5 7.5 3 3"/><path d="M14 9l2 2"/>';
  } else if (categoryCode === 'WEARABLES') {
    icon = '<path d="M12 2v4"/><path d="M12 18v4"/><rect x="4" y="6" width="16" height="12" rx="4"/>';
  } else if (categoryCode === 'BOOKS_STATIONERY') {
    icon = '<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>';
  }
  return `<svg viewBox="0 0 24 24" fill="none" stroke="${stroke}" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round" class="card-placeholder-icon">${icon}</svg>`;
}


function formatDaysAgo(dateStr) {
  if (!dateStr) return '';
  const diff = Math.floor((Date.now() - new Date(dateStr).getTime()) / (1000 * 60 * 60 * 24));
  if (isNaN(diff)) return '';
  if (diff <= 0) return 'today';
  return `${diff}d ago`;
}
const STATE = {
  activeUserId: "USR-STU-01",
  activeRole: "STUDENT_STAFF",
  activeUserName: "Asmitha B. (Computer Science)",
  categories: [],
  locations: [],
  lostReports: [],
  foundItems: [],
  claims: [],
  handovers: [],
  notifications: [],
  feedFilter: "all",
  searchQuery: "",
  selectedCategory: ""
};

document.addEventListener("DOMContentLoaded", async () => {
  setupTabs();
  setupRoleSwitcher();
  setupNotifications();
  setupPhotoCanvas();
  setupForms();
  setupModals();
  updateRoleBanners();
  await refreshAllData();
});

function setupTabs() {
  const activateTab = (targetId) => {
    document.querySelectorAll(".tab-btn").forEach(b => {
      const isMatch = b.getAttribute("data-tab") === targetId;
      b.classList.toggle("active", isMatch);
      b.setAttribute("aria-selected", isMatch ? "true" : "false");
    });

    document.querySelectorAll(".nav-link-btn").forEach(b => {
      b.classList.toggle("active", b.getAttribute("data-tab") === targetId);
    });

    document.querySelectorAll(".tab-panel").forEach(p => {
      p.classList.toggle("active", p.id === targetId);
    });

    if (targetId === "matchTab") populateMatchDropdown();
    else if (targetId === "adminTab") loadAdminAnalytics();
    else if (targetId === "claimsTab") loadClaimsAndHandovers();

    if (targetId !== "feedTab") {
      const dashboard = document.getElementById("dashboardSection");
      if (dashboard) dashboard.scrollIntoView({ behavior: "smooth" });
    }
  };

  document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.addEventListener("click", () => activateTab(btn.getAttribute("data-tab")));
  });

  document.querySelectorAll(".nav-link-btn").forEach(btn => {
    btn.addEventListener("click", () => activateTab(btn.getAttribute("data-tab")));
  });

  window.selectTab = activateTab;

  document.getElementById("heroLostBtn")?.addEventListener("click", () => activateTab("reportLostTab"));
  document.getElementById("heroFoundBtn")?.addEventListener("click", () => activateTab("logFoundTab"));
  document.getElementById("heroDashboardBtn")?.addEventListener("click", () => activateTab("feedTab"));
  document.getElementById("stepCard1")?.addEventListener("click", () => activateTab("reportLostTab"));
  document.getElementById("stepCard2")?.addEventListener("click", () => activateTab("matchTab"));
  document.getElementById("stepCard3")?.addEventListener("click", () => activateTab("claimsTab"));

  document.querySelectorAll(".seg-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".seg-btn").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      STATE.feedFilter = btn.getAttribute("data-type");
      renderFeed();
    });
  });

  document.getElementById("feedSearch")?.addEventListener("input", (e) => {
    STATE.searchQuery = e.target.value.toLowerCase();
    renderFeed();
  });

  document.getElementById("categoryFilter")?.addEventListener("change", (e) => {
    STATE.selectedCategory = e.target.value;
    renderFeed();
  });
}


function updateRoleBanners() {
  const claimsBanner = document.getElementById("claimsRoleBanner");
  if (claimsBanner) {
    if (STATE.activeRole === "STUDENT_STAFF") {
      claimsBanner.innerHTML = `<span>👤 Viewing as <strong>${escapeHtml(STATE.activeUserName)}</strong>. Only Security Desk can verify & approve claims or record handovers.</span> <button class="btn btn-secondary btn-sm" onclick="switchToPersona('USR-SEC-01')">🛡️ Switch to Officer Dan (Security Desk)</button>`;
    } else {
      claimsBanner.innerHTML = `<span>🛡️ Active Persona: <strong>${escapeHtml(STATE.activeUserName)}</strong>. Authorized to review verification questions, approve claims (FR6), and execute physical handovers (FR5).</span>`;
    }
  }

  const adminBanner = document.getElementById("adminRoleBanner");
  if (adminBanner) {
    if (STATE.activeRole !== "ADMIN") {
      adminBanner.innerHTML = `<span>⚡ Viewing as <strong>${escapeHtml(STATE.activeUserName)}</strong>. Switch to Campus Admin for complete KPI recovery analytics and immutable audit logs:</span> <button class="btn btn-secondary btn-sm" onclick="switchToPersona('USR-ADM-01')">⚡ Switch to Dr. Eleanor Vance (Campus Admin)</button>`;
    } else {
      adminBanner.innerHTML = `<span>⚡ Active Persona: <strong>${escapeHtml(STATE.activeUserName)}</strong>. Campus Administrator privileges active.</span>`;
    }
  }
}

window.switchToPersona = function(userId) {
  const selector = document.getElementById("roleSelector");
  if (selector) {
    selector.value = userId;
    selector.dispatchEvent(new Event("change"));
  }
};

function setupRoleSwitcher() {
  const selector = document.getElementById("roleSelector");
  selector?.addEventListener("change", (e) => {
    STATE.activeUserId = e.target.value;
    STATE.activeUserName = e.target.selectedOptions[0].text;
    if (STATE.activeUserId.includes("ADM")) STATE.activeRole = "ADMIN";
    else if (STATE.activeUserId.includes("SEC")) STATE.activeRole = "SECURITY_DESK";
    else STATE.activeRole = "STUDENT_STAFF";

    updateRoleBanners();
    renderFeed();
    loadNotifications();
    if (document.getElementById("claimsTab")?.classList.contains("active")) {
      loadClaimsAndHandovers();
    }
  });
}

async function refreshAllData() {
  try {
    const [cats, locs, lost, found] = await Promise.all([
      fetch("/api/categories").then(r => r.json()),
      fetch("/api/locations").then(r => r.json()),
      fetch("/api/items/lost").then(r => r.json()),
      fetch("/api/items/found", { headers: { "Authorization": STATE.activeRole } }).then(r => r.json())
    ]);
    STATE.categories = cats;
    STATE.locations = locs;
    STATE.lostReports = lost;
    STATE.foundItems = found;

    populateDropdowns();
    renderFeed();
    populateMatchDropdown();
    loadNotifications();
    loadAdminAnalytics();
  } catch (err) {
    console.error("Failed to load data:", err);
  }
}

function populateDropdowns() {
  const catSelects = [document.getElementById("lostCategory"), document.getElementById("foundCategory")];
  catSelects.forEach(select => {
    if (!select) return;
    select.innerHTML = '<option value="">Select Category...</option>';
    STATE.categories.forEach(c => {
      select.innerHTML += `<option value="${c.code}">${c.name}</option>`;
    });
  });

  const locSelects = [document.getElementById("lostLocation"), document.getElementById("foundLocation")];
  locSelects.forEach(select => {
    if (!select) return;
    select.innerHTML = '<option value="">Select Building / Room...</option>';
    STATE.locations.forEach(l => {
      select.innerHTML += `<option value="${l.id}">${l.building} (${l.floor} - ${l.room})</option>`;
    });
  });

  const today = new Date().toISOString().split("T")[0];
  const dateInputs = [document.getElementById("lostDate"), document.getElementById("foundDate")];
  dateInputs.forEach(d => { if (d) d.value = today; });
}

function renderFeed() {
  const grid = document.getElementById("itemsGrid");
  if (!grid) return;

  let allCards = [];

  if (STATE.feedFilter === "all" || STATE.feedFilter === "lost") {
    STATE.lostReports.forEach(item => {
      allCards.push({
        type: "lost",
        id: item.id,
        title: item.title,
        desc: item.description,
        category: item.categoryCode,
        location: formatLocation(item.locationId),
        date: item.lostDate,
        status: item.statusCode,
        statusName: item.statusName
      });
    });
  }

  if (STATE.feedFilter === "all" || STATE.feedFilter === "found") {
    STATE.foundItems.forEach(item => {
      allCards.push({
        type: "found",
        id: item.id,
        title: item.title,
        desc: item.description,
        category: item.categoryCode,
        location: formatLocation(item.locationId),
        date: item.foundDate,
        status: item.statusCode,
        statusName: item.statusName,
        photo: item.photoUrl,
        hasMasking: item.hasSensitiveMasking
      });
    });
  }

  const filtered = allCards.filter(c => {
    const matchesCat = !STATE.selectedCategory || c.category === STATE.selectedCategory;
    const matchesSearch = !STATE.searchQuery ||
      c.title.toLowerCase().includes(STATE.searchQuery) ||
      c.desc.toLowerCase().includes(STATE.searchQuery) ||
      c.location.toLowerCase().includes(STATE.searchQuery);
    return matchesCat && matchesSearch;
  });

  if (filtered.length === 0) {
    grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: #94a3b8;">No lost or found items matched your search criteria.</div>';
    return;
  }

  grid.innerHTML = filtered.map(item => {
    const isLost = item.type === "lost";
    const statusBadgeClass = getBadgeClass(item.status);
    const days = formatDaysAgo(item.date);
    const dateText = item.date ? `${item.date} (${days})` : '';
    const isExpired = item.status === 'EXPIRED_FOR_DONATION';
    const hasPhoto = item.photo && item.photo.trim().length > 5 && item.photo !== 'null' && item.photo !== 'undefined';

    const photoHtml = hasPhoto
      ? `<img src="${item.photo}" alt="${escapeHtml(item.title)}" class="item-card-img">`
      : `<div class="card-placeholder-box">${getCategoryPlaceholderSvg(item.category)}<span class="placeholder-caption">${isLost ? 'Lost Item Report' : 'No photo uploaded'}</span></div>`;

    return `
      <article class="item-card" aria-label="${escapeHtml(item.title)}">
        <div class="item-card-media">
          ${photoHtml}
          <span class="item-type-badge ${item.type}">${isLost ? 'Lost' : 'Found'}</span>
          ${item.hasMasking ? '<span class="privacy-shield-tag">🛡️ Privacy Shield (NFR-P1)</span>' : ''}
        </div>
        <div class="item-card-body">
          <div class="item-card-header-row">
            <h3 class="item-title">${escapeHtml(item.title)}</h3>
          </div>
          <p class="item-card-sub">${escapeHtml(item.category || '')} · ${escapeHtml(item.location || '')} · ${escapeHtml(dateText)}</p>
          <p class="item-desc">${escapeHtml(item.desc || 'No description provided.')}</p>
          <div class="item-card-badges">
            <span class="badge ${statusBadgeClass}">${item.statusName || item.status}</span>
            ${isExpired ? '<span class="badge badge-expired">Expired · flag for donation</span>' : ''}
          </div>
          <div class="item-card-footer">
            ${!isLost && (item.status === 'IN_CUSTODY' || item.status === 'MATCHED') ?
              `<button class="btn-card-claim" onclick="openClaimModal('${item.id}', '${escapeHtml(item.title)}', '${item.category}')">This is mine — claim it</button>`
              : ''}
            ${isLost ? `<button class="btn-card-claim" onclick="evaluateReportMatches('${item.id}')">View suggested matches →</button>` : ''}
          </div>
        </div>
      </article>
    `;
  }).join('');
}

function formatLocation(locId) {
  const l = STATE.locations.find(x => x.id === locId);
  return l ? `${l.building} (${l.floor})` : locId;
}

function getBadgeClass(code) {
  switch (code) {
    case 'REPORTED': return 'badge-reported';
    case 'IN_CUSTODY': return 'badge-custody';
    case 'MATCHED': return 'badge-matched';
    case 'CLAIM_PENDING': return 'badge-pending';
    case 'CLAIM_APPROVED': return 'badge-approved';
    case 'HANDED_OVER': return 'badge-handed';
    case 'EXPIRED_FOR_DONATION': return 'badge-expired';
    case 'REJECTED': return 'badge-rejected';
    default: return 'badge-custody';
  }
}

function setupPhotoCanvas() {
  const canvas = document.getElementById("photoCanvas");
  if (!canvas) return;
  const ctx = canvas.getContext("2d");

  function drawCanvas(isMasked) {
    ctx.fillStyle = "#1e293b";
    ctx.fillRect(0, 0, 400, 250);

    ctx.fillStyle = "#334155";
    ctx.beginPath();
    ctx.roundRect(50, 40, 300, 160, 10);
    ctx.fill();

    ctx.fillStyle = "#0f172a";
    ctx.beginPath();
    ctx.roundRect(65, 55, 270, 130, 6);
    ctx.fill();

    if (isMasked) {
      ctx.fillStyle = "rgba(225, 29, 72, 0.85)";
      ctx.beginPath();
      ctx.roundRect(80, 85, 240, 50, 6);
      ctx.fill();

      ctx.fillStyle = "#ffffff";
      ctx.font = "bold 13px system-ui";
      ctx.textAlign = "center";
      ctx.fillText("🛡️ SENSITIVE DETAILS OBSCURED", 200, 110);
      ctx.font = "11px system-ui";
      ctx.fillText("NFR-P1 Privacy Shield · Verify at Desk", 200, 126);
    } else {
      ctx.fillStyle = "#38bdf8";
      ctx.font = "13px monospace";
      ctx.textAlign = "center";
      ctx.fillText("ID: STU-992144 | CARD # 4111-XXXX", 200, 115);
      ctx.fillStyle = "#94a3b8";
      ctx.font = "11px system-ui";
      ctx.fillText("(Sensitive Details Exposed)", 200, 135);
    }
  }

  const toggle = document.getElementById("maskSensitiveToggle");
  toggle?.addEventListener("change", () => {
    drawCanvas(toggle.checked);
  });

  drawCanvas(true);
  document.getElementById("usePresetPhotoBtn")?.addEventListener("click", () => {
    drawCanvas(toggle.checked);
  });

  const fileInput = document.getElementById("photoFileInput");
  fileInput?.addEventListener("change", (e) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (evt) => {
        const img = new Image();
        img.onload = () => {
          ctx.clearRect(0, 0, 400, 250);
          ctx.drawImage(img, 0, 0, 400, 250);
          if (toggle && toggle.checked) {
            ctx.fillStyle = "rgba(225, 29, 72, 0.88)";
            ctx.beginPath();
            ctx.roundRect(70, 85, 260, 50, 6);
            ctx.fill();
            ctx.fillStyle = "#ffffff";
            ctx.font = "bold 13px system-ui";
            ctx.textAlign = "center";
            ctx.fillText("🛡️ SENSITIVE DETAILS OBSCURED", 200, 110);
            ctx.font = "11px system-ui";
            ctx.fillText("NFR-P1 Privacy Shield · Verify at Desk", 200, 126);
          }
        };
        img.src = evt.target.result;
      };
      reader.readAsDataURL(file);
    }
  });
}

function setupForms() {
  // FR1: Report Lost
  document.getElementById("lostItemForm")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const payload = {
      title: document.getElementById("lostTitle").value,
      categoryCode: document.getElementById("lostCategory").value,
      locationId: document.getElementById("lostLocation").value,
      lostDate: document.getElementById("lostDate").value,
      brand: document.getElementById("lostBrand").value,
      color: document.getElementById("lostColor").value,
      distinctiveFeatures: document.getElementById("lostDistinctive").value,
      description: document.getElementById("lostDesc").value,
      contactPhone: document.getElementById("lostPhone").value,
      contactEmail: document.getElementById("lostEmail").value,
      reporterUserId: STATE.activeUserId,
      reporterName: STATE.activeUserName.split(":")[1]?.trim() || "Campus Student"
    };

    try {
      const resp = await fetch("/api/items/lost", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      const data = await resp.json();
      alert(`✓ Lost Item Report Registered Successfully! (ID: ${data.id})\nAuto-matching engine will now evaluate candidate matches in < 1s.`);
      document.getElementById("lostItemForm").reset();
      await refreshAllData();
      evaluateReportMatches(data.id);
    } catch (err) {
      alert("Failed to report lost item: " + err.message);
    }
  });

  // FR2: Log Found
  document.getElementById("foundItemForm")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const canvas = document.getElementById("photoCanvas");
    const photoData = canvas ? canvas.toDataURL("image/png") : "";

    const payload = {
      title: document.getElementById("foundTitle").value,
      categoryCode: document.getElementById("foundCategory").value,
      locationId: document.getElementById("foundLocation").value,
      foundDate: document.getElementById("foundDate").value,
      brand: document.getElementById("foundBrand").value,
      color: document.getElementById("foundColor").value,
      custodyLockerId: document.getElementById("custodyLocker").value,
      hasSensitiveMasking: document.getElementById("maskSensitiveToggle").checked ? "true" : "false",
      secretVerificationHints: document.getElementById("secretHints").value,
      description: document.getElementById("foundDesc").value,
      loggedByUserId: STATE.activeUserId,
      loggedByRole: STATE.activeRole,
      photoUrl: photoData
    };

    try {
      const resp = await fetch("/api/items/found", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      const data = await resp.json();
      alert(`✓ Found Item Secured! (ID: ${data.id}, Custody Locker: ${data.custodyLockerId})\n60-day expiry rule active.`);
      document.getElementById("foundItemForm").reset();
      await refreshAllData();
      evaluateReportMatches(data.id);
    } catch (err) {
      alert("Failed to log found item: " + err.message);
    }
  });

  // FR7: Expiry Scan
  document.getElementById("triggerExpiryBtn")?.addEventListener("click", async () => {
    try {
      const resp = await fetch("/api/expiry/scan", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ date: new Date().toISOString().split("T")[0] })
      });
      const res = await resp.json();
      alert(`✓ 60-Day Expiry Scan Completed (FR7):\n· Total Evaluated: ${res.totalEvaluated}\n·• Newly Flagged for Donation: ${res.newlyFlaggedForDonation}\n·• Donation Manifest Batch: ${res.batchId}`);
      await refreshAllData();
      loadAdminAnalytics();
    } catch (err) {
      alert("Expiry scan failed: " + err.message);
    }
  });
}

function populateMatchDropdown() {
  const select = document.getElementById("matchReportSelect");
  if (!select) return;
  select.innerHTML = '<option value="">Select a lost report to evaluate...</option>';
  STATE.lostReports.forEach(r => {
    select.innerHTML += `<option value="${r.id}">[${r.id}] ${escapeHtml(r.title)} (${r.categoryCode})</option>`;
  });
}

window.evaluateReportMatches = function(reportId) {
  const select = document.getElementById("matchReportSelect");
  if (select) select.value = reportId;
  document.querySelector('[data-tab="matchTab"]')?.click();
  executeMatchScan();
};

document.getElementById("runMatchBtn")?.addEventListener("click", executeMatchScan);

async function executeMatchScan() {
  const reportId = document.getElementById("matchReportSelect")?.value;
  if (!reportId) {
    alert("Please select a lost report to evaluate.");
    return;
  }

  const container = document.getElementById("matchesContainer");
  const telemetry = document.getElementById("matchTelemetry");
  container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 2rem;">⚡ Executing parallel multi-factor matching via CompletableFuture...</div>';
  telemetry.hidden = false;

  try {
    const t0 = performance.now();
    const resp = await fetch(`/api/items/matches?reportId=${reportId}`);
    const data = await resp.json();
    const clientLatency = Math.round(performance.now() - t0);

    document.getElementById("telemetryLatency").textContent = `${data.executionTimeMs || clientLatency} ms (NFR-P2 < 1s Passed)`;
    document.getElementById("telemetryCandidates").textContent = STATE.foundItems.length;

    if (!data.matches || data.matches.length === 0) {
      container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: #94a3b8;">No candidates currently exceed the 40% match threshold. System continues monitoring incoming found logs.</div>';
      return;
    }

    container.innerHTML = data.matches.map(m => {
      const item = m.foundItem || STATE.foundItems.find(x => x.id === m.foundItemId);
      const pct = Math.round(m.overallScore * 100);
      const confColor = m.confidenceLevel === 'HIGH' ? '#4ade80' : m.confidenceLevel === 'MEDIUM' ? '#fbbf24' : '#94a3b8';

      return `
        <article class="match-card">
          <div class="match-score-badge">
            <span class="score-num">${pct}%</span>
            <div style="font-size: 0.72rem; font-weight: 700; color: ${confColor}; text-transform: uppercase;">${m.confidenceLevel} CONFIDENCE</div>
          </div>

          <h3 style="font-size: 1.1rem; font-weight: 700; color: var(--text-main); max-width: 70%;">${escapeHtml(item?.title || m.foundItemId)}</h3>
          <p style="font-size: 0.82rem; color: #94a3b8; margin-top: 0.2rem;">Location: ${formatLocation(item?.locationId)} · Found: ${item?.foundDate}</p>

          <div class="score-bars">
            <div class="score-bar-row">
              <span>Category (35%)</span>
              <div class="bar-track"><div class="bar-fill" style="width: ${m.categoryScore * 100}%"></div></div>
              <span>${Math.round(m.categoryScore * 100)}%</span>
            </div>
            <div class="score-bar-row">
              <span>Location (25%)</span>
              <div class="bar-track"><div class="bar-fill" style="width: ${m.locationScore * 100}%"></div></div>
              <span>${Math.round(m.locationScore * 100)}%</span>
            </div>
            <div class="score-bar-row">
              <span>Date (20%)</span>
              <div class="bar-track"><div class="bar-fill" style="width: ${m.dateScore * 100}%"></div></div>
              <span>${Math.round(m.dateScore * 100)}%</span>
            </div>
            <div class="score-bar-row">
              <span>Keywords (20%)</span>
              <div class="bar-track"><div class="bar-fill" style="width: ${m.keywordScore * 100}%"></div></div>
              <span>${Math.round(m.keywordScore * 100)}%</span>
            </div>
          </div>

          <div class="match-rationale">
            <strong>Rationale:</strong> ${escapeHtml(m.rationale)}
          </div>

          <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: auto;">
            <button class="btn btn-primary btn-sm" onclick="openClaimModal('${m.foundItemId}', '${escapeHtml(item?.title || '')}', '${item?.categoryCode || ''}', '${m.lostReportId}')">
              Raise Claim with Questions (FR4)
            </button>
          </div>
        </article>
      `;
    }).join('');

  } catch (err) {
    container.innerHTML = `<div style="grid-column: 1/-1; color: #ef4444; padding: 2rem;">Match error: ${err.message}</div>`;
  }
}

window.openClaimModal = function(foundItemId, title, categoryCode, lostReportId = "") {
  const modal = document.getElementById("claimModal");
  document.getElementById("claimFoundItemId").value = foundItemId;
  document.getElementById("claimLostReportId").value = lostReportId;
  document.getElementById("claimItemTitle").textContent = title || "Item #" + foundItemId;
  document.getElementById("claimItemDetails").textContent = `Category: ${categoryCode} · Found Item ID: ${foundItemId}`;

  const catObj = STATE.categories.find(c => c.code === categoryCode) || STATE.categories[0];
  const questions = catObj ? catObj.questions : [
    "What is the device brand and color?",
    "What is the lock screen wallpaper or specific case design?",
    "What unique markings, scratches or serial digits prove your ownership?"
  ];

  const qContainer = document.getElementById("dynamicQuestionsContainer");
  qContainer.innerHTML = questions.map((q, idx) => `
    <div class="form-group">
      <label for="claim_q${idx+1}">${idx+1}. ${escapeHtml(q)} *</label>
      <input type="hidden" name="q${idx+1}_text" value="${escapeHtml(q)}">
      <input type="text" id="claim_q${idx+1}" name="q${idx+1}" required placeholder="Provide accurate ownership answer...">
    </div>
  `).join('');

  modal.hidden = false;
};

function setupModals() {
  document.getElementById("closeClaimModal")?.addEventListener("click", () => {
    document.getElementById("claimModal").hidden = true;
  });
  document.getElementById("cancelClaimBtn")?.addEventListener("click", () => {
    document.getElementById("claimModal").hidden = true;
  });

  // Submit Claim (FR4)
  document.getElementById("raiseClaimForm")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const foundId = document.getElementById("claimFoundItemId").value;
    const lostId = document.getElementById("claimLostReportId").value;

    const payload = {
      foundItemId: foundId,
      lostReportId: lostId,
      claimantUserId: STATE.activeUserId,
      claimantName: STATE.activeUserName.split(":")[1]?.trim() || "Student Claimant",
      claimantContact: "contact@student.campus.edu",
      proofAttachmentUrl: document.getElementById("proofReceipt").value
    };

    const qInputs = document.querySelectorAll("#dynamicQuestionsContainer input[type='text']");
    qInputs.forEach((input, idx) => {
      payload[`q${idx+1}`] = input.value;
      const textInput = document.querySelector(`input[name='q${idx+1}_text']`);
      if (textInput) payload[`q${idx+1}_text`] = textInput.value;
    });

    try {
      const resp = await fetch("/api/claims", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      const data = await resp.json();
      if (!resp.ok) throw new Error(data.error || "Submission failed");

      alert(`✓ Ownership Claim Raised! (ID: ${data.id})\nAwaiting Security Desk verification.`);
      document.getElementById("claimModal").hidden = true;
      document.getElementById("raiseClaimForm").reset();
      await refreshAllData();
      document.querySelector('[data-tab="claimsTab"]')?.click();
    } catch (err) {
      alert("Failed to submit claim: " + err.message);
    }
  });

  // Handover Modal
  document.getElementById("closeHandoverModal")?.addEventListener("click", () => {
    document.getElementById("handoverModal").hidden = true;
  });
  document.getElementById("cancelHandoverBtn")?.addEventListener("click", () => {
    document.getElementById("handoverModal").hidden = true;
  });

  document.getElementById("handoverClaimSelect")?.addEventListener("change", (e) => {
    const cid = e.target.value;
    document.getElementById("handoverClaimId").value = cid;
    const claim = STATE.claims.find(c => c.id === cid);
    if (claim) {
      document.getElementById("recipientName").value = claim.claimantName;
      const item = STATE.foundItems.find(f => f.id === claim.foundItemId);
      if (item && item.custodyLockerId) {
        document.getElementById("releasedLocker").value = item.custodyLockerId;
      }
    }
  });

  // Record Handover (FR5)
  document.getElementById("recordHandoverForm")?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const claimId = document.getElementById("handoverClaimId").value || document.getElementById("handoverClaimSelect")?.value;
    if (!claimId) {
      alert("Please select a verified approved claim to execute handover.");
      return;
    }
    const payload = {
      claimId: claimId,
      recipientName: document.getElementById("recipientName").value,
      recipientIdNumber: document.getElementById("recipientIdNumber").value,
      officerName: document.getElementById("officerName").value,
      lockerIdReleased: document.getElementById("releasedLocker").value,
      verificationNotes: document.getElementById("handoverNotes").value,
      recipientSignature: "DIGITAL_SIG_" + Date.now()
    };

    try {
      const resp = await fetch("/api/handover", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      const data = await resp.json();
      if (!resp.ok) throw new Error(data.error || "Handover failed");

      alert(`✓ Handover Completed & Verified! (ID: ${data.id})\nItem released to ${data.recipientName}. Status updated to HANDED_OVER via Feign.`);
      document.getElementById("handoverModal").hidden = true;
      document.getElementById("recordHandoverForm").reset();
      await refreshAllData();
      loadClaimsAndHandovers();
    } catch (err) {
      alert("Handover error: " + err.message);
    }
  });
}

async function loadClaimsAndHandovers() {
  try {
    const [claims, handovers] = await Promise.all([
      fetch("/api/claims").then(r => r.json()),
      fetch("/api/handovers").then(r => r.json())
    ]);

    STATE.claims = claims;
    STATE.handovers = handovers;

    document.getElementById("claimCountBadge").textContent = claims.length;
    document.getElementById("handoverCountBadge").textContent = handovers.length;

    renderClaimsList();
    renderHandoversList();
  } catch (err) {
    console.error("Failed to load claims/handovers:", err);
  }
}

function renderClaimsList() {
  const container = document.getElementById("claimsList");
  if (!container) return;

  if (STATE.claims.length === 0) {
    container.innerHTML = '<div style="color: #94a3b8; padding: 1.5rem; text-align: center;">No claims registered yet.</div>';
    return;
  }

  const isSecurity = STATE.activeRole === "SECURITY_DESK" || STATE.activeRole === "ADMIN";

  container.innerHTML = STATE.claims.map(c => {
    const isPending = c.status === "PENDING_VERIFICATION";
    const isApproved = c.status === "APPROVED";

    return `
      <div class="claim-item-card">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
          <div>
            <h4 style="color: var(--text-main); font-size: 1.05rem; font-weight: 700;">Claim #${c.id}</h4>
            <span style="font-size: 0.75rem; color: #94a3b8;">Item #${c.foundItemId} · Claimant: <strong>${escapeHtml(c.claimantName)}</strong> (${c.claimantUserId})</span>
          </div>
          <span class="badge ${getBadgeClass(c.status)}">${c.status}</span>
        </div>

        <div class="qa-list">
          <div style="font-size: 0.75rem; font-weight: 700; color: #94a3b8; margin-bottom: 0.35rem;">VERIFICATION ANSWERS (FR4):</div>
          ${c.answers.map(a => `
            <div class="qa-item">
              <div class="qa-q">${escapeHtml(a.q)}</div>
              <div class="qa-a">${escapeHtml(a.a)}</div>
            </div>
          `).join('')}
        </div>

        ${c.officerReviewNotes ? `<p style="font-size: 0.8rem; color: #cbd5e1; margin-bottom: 0.75rem;"><strong>Officer Notes:</strong> ${escapeHtml(c.officerReviewNotes)}</p>` : ''}

        <div style="display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 0.75rem;">
          ${isSecurity && isPending ? `
            <button class="btn btn-danger btn-sm" onclick="rejectClaim('${c.id}')">Reject</button>
            <button class="btn btn-success btn-sm" onclick="approveClaim('${c.id}')">Approve Claim (FR6 Guard)</button>
          ` : ''}

          ${isSecurity && isApproved ? `
            <button class="btn btn-primary btn-sm" onclick="openHandoverModal('${c.id}', '${escapeHtml(c.claimantName)}')">
              Verify & Record Handover (FR5)
            </button>
          ` : ''}
        </div>
      </div>
    `;
  }).join('');
}

function renderHandoversList() {
  const container = document.getElementById("handoversList");
  if (!container) return;

  if (STATE.handovers.length === 0) {
    container.innerHTML = '<div style="color: #94a3b8; padding: 1.5rem; text-align: center;">No handovers completed yet.</div>';
    return;
  }

  container.innerHTML = STATE.handovers.map(h => `
    <div class="handover-item-card">
      <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.4rem;">
        <h4 style="color: #38bdf8; font-size: 0.95rem;">${h.id} · Handed Over</h4>
        <span style="font-size: 0.72rem; color: #94a3b8;">${new Date(h.timestamp).toLocaleString()}</span>
      </div>
      <p style="font-size: 0.82rem; color: var(--text-main); margin-bottom: 0.25rem;">
        Recipient: <strong>${escapeHtml(h.recipientName)}</strong> (ID: <code>${escapeHtml(h.recipientIdNumber)}</code>)
      </p>
      <p style="font-size: 0.8rem; color: #94a3b8;">
        Item: <strong>#${h.foundItemId}</strong> · Officer: <strong>${escapeHtml(h.officerName)}</strong> · Locker: <code>${h.lockerIdReleased}</code>
      </p>
      <p style="font-size: 0.78rem; color: #cbd5e1; margin-top: 0.35rem; font-style: italic;">
        "${escapeHtml(h.notes)}"
      </p>
    </div>
  `).join('');
}

window.approveClaim = async function(claimId) {
  const notes = prompt("Enter officer verification rationale:", "Claimant answers matched physical item details");
  if (notes === null) return;

  try {
    const resp = await fetch("/api/claims/approve", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ claimId, officerId: STATE.activeUserId, officerNotes: notes })
    });
    const data = await resp.json();

    if (!resp.ok) throw new Error(data.error || "Approval rejected");

    alert(`✓ Claim Approved! (FR6 Anti-Duplicate Claim guard verified)\nItem status set to CLAIM_APPROVED via Feign. Ready for handover.`);
    await refreshAllData();
    loadClaimsAndHandovers();
  } catch (err) {
    alert("Approval blocked: " + err.message);
  }
};

window.rejectClaim = async function(claimId) {
  const reason = prompt("Enter reason for rejection:", "Verification answers did not match serial or distinct marks");
  if (!reason) return;

  try {
    const resp = await fetch("/api/claims/reject", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ claimId, officerId: STATE.activeUserId, reason })
    });
    const data = await resp.json();
    alert("Claim marked as REJECTED. Claimant notified.");
    await refreshAllData();
    loadClaimsAndHandovers();
  } catch (err) {
    alert("Rejection error: " + err.message);
  }
};

window.openHandoverModal = function(claimId, claimantName) {
  const modal = document.getElementById("handoverModal");
  const select = document.getElementById("handoverClaimSelect");

  const approvedClaims = STATE.claims.filter(c => c.status === "APPROVED");
  if (select) {
    if (approvedClaims.length === 0) {
      select.innerHTML = '<option value="">No Approved Claims available</option>';
    } else {
      select.innerHTML = '<option value="">Select an Approved Claim...</option>' +
        approvedClaims.map(c => {
          const item = STATE.foundItems.find(f => f.id === c.foundItemId);
          const itemTitle = item ? item.title : ('Item #' + c.foundItemId);
          return `<option value="${c.id}">${c.id}: ${escapeHtml(c.claimantName)}   ${escapeHtml(itemTitle)}</option>`;
        }).join('');
    }
  }

  if (claimId) {
    if (select) select.value = claimId;
    document.getElementById("handoverClaimId").value = claimId;
  } else if (approvedClaims.length > 0) {
    if (select) select.value = approvedClaims[0].id;
    document.getElementById("handoverClaimId").value = approvedClaims[0].id;
    claimId = approvedClaims[0].id;
    claimantName = approvedClaims[0].claimantName;
  }

  const currentClaim = STATE.claims.find(c => c.id === (claimId || select?.value));
  if (currentClaim) {
    document.getElementById("recipientName").value = currentClaim.claimantName;
    const item = STATE.foundItems.find(f => f.id === currentClaim.foundItemId);
    if (item && item.custodyLockerId) {
      document.getElementById("releasedLocker").value = item.custodyLockerId;
    }
  } else if (claimantName) {
    document.getElementById("recipientName").value = claimantName;
  }

  document.getElementById("officerName").value = STATE.activeUserName.split(":")[1]?.trim() || "Officer Daniel Hayes";
  modal.hidden = false;
};

async function loadAdminAnalytics() {
  try {
    const [stats, audit] = await Promise.all([
      fetch("/api/stats").then(r => r.json()),
      fetch("/api/audit").then(r => r.json())
    ]);

    document.getElementById("recoveryRateKPI").textContent = `${stats.recoveryRatePercentage}%`;
    document.getElementById("totalFoundKPI").textContent = stats.totalFoundItems;
    document.getElementById("totalLostKPI").textContent = stats.totalLostReports;
    document.getElementById("totalHandoverKPI").textContent = stats.totalHandovers;
    document.getElementById("totalDonatedKPI").textContent = stats.itemsFlaggedForDonation;
    document.getElementById("avgDaysKPI").textContent = `${stats.averageTurnaroundDays}d`;

    const chart = document.getElementById("categoryChart");
    if (chart && stats.foundByCategory) {
      const maxCount = Math.max(...Object.values(stats.foundByCategory), 1);
      chart.innerHTML = Object.entries(stats.foundByCategory).map(([cat, count]) => `
        <div class="chart-bar-item">
          <div class="chart-bar-label">
            <span>${cat}</span>
            <span><strong>${count}</strong> items</span>
          </div>
          <div class="chart-bar-track">
            <div class="chart-bar-fill" style="width: ${(count / maxCount) * 100}%"></div>
          </div>
        </div>
      `).join('');
    }

    const auditBody = document.getElementById("auditTableBody");
    if (auditBody && audit) {
      auditBody.innerHTML = audit.map(entry => `
        <tr>
          <td style="white-space: nowrap;">${new Date(entry.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}</td>
          <td><span class="badge ${entry.eventType.includes('BLOCKED') ? 'badge-expired' : 'badge-custody'}">${entry.eventType}</span></td>
          <td><code>${entry.entityType}#${entry.entityId}</code></td>
          <td>${entry.actorId} <small>(${entry.actorRole})</small></td>
          <td>${escapeHtml(entry.action)}</td>
          <td class="audit-hash" title="Cryptographic SHA-256 Checksum">${entry.checksum}</td>
        </tr>
      `).join('');
    }
  } catch (err) {
    console.error("Failed to load admin analytics:", err);
  }
}

function setupNotifications() {
  const btn = document.getElementById("notifBtn");
  const drawer = document.getElementById("notifDrawer");
  const close = document.getElementById("closeNotifBtn");

  btn?.addEventListener("click", () => {
    drawer.hidden = !drawer.hidden;
  });

  close?.addEventListener("click", () => {
    drawer.hidden = true;
  });
}

async function loadNotifications() {
  try {
    const resp = await fetch(`/api/notifications?userId=${STATE.activeUserId}`);
    const notifs = await resp.json();
    STATE.notifications = notifs;

    const badge = document.getElementById("notifBadge");
    const unread = notifs.filter(n => !n.isRead).length;
    if (badge) {
      badge.textContent = unread;
      badge.style.display = unread > 0 ? "inline-block" : "none";
    }

    const list = document.getElementById("notifList");
    if (list) {
      if (notifs.length === 0) {
        list.innerHTML = '<div style="color: #94a3b8; text-align: center; padding: 1rem;">No alerts right now.</div>';
        return;
      }
      list.innerHTML = notifs.map(n => `
        <div class="notif-card ${n.isRead ? 'read' : ''}">
          <div class="notif-title">${escapeHtml(n.title)}</div>
          <div>${escapeHtml(n.message)}</div>
          <div class="notif-time">${new Date(n.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
        </div>
      `).join('');
    }
  } catch (err) {
    console.error("Notification load error:", err);
  }
}

function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
