const STORAGE_KEY = "carols_catalog_items";

function getCatalog() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(SEED_ITEMS));
    return [...SEED_ITEMS];
  }
  try {
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [...SEED_ITEMS];
  } catch (error) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(SEED_ITEMS));
    return [...SEED_ITEMS];
  }
}

function saveCatalog(items) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
}

function getCategoryInfo(categoryName) {
  return CATEGORY_INFO.find((item) => item.category === categoryName);
}

function nextCode(items) {
  const max = items.reduce((acc, item) => {
    const value = Number(item.code);
    return Number.isFinite(value) ? Math.max(acc, value) : acc;
  }, 999);
  return String(max + 1);
}

function buildSeo(item) {
  return `${item.code}_${item.category}`.toLowerCase();
}

function setActiveNav(pageType) {
  document.querySelectorAll("[data-nav]").forEach((link) => {
    link.classList.toggle("is-active", link.getAttribute("data-nav") === pageType);
  });
}

function createCategoryCard(category) {
  return `<a class="card" href="${category.page}"><div class="card-media"><img src="${category.image}" alt="${category.title}"></div><div class="card-copy"><h3>${category.title}</h3><p>${category.description}</p></div></a>`;
}

function createProductCard(item) {
  return `<article class="catalog-item"><div class="catalog-item-figure"><img src="${item.image}" alt="${item.title}"><div class="catalog-code">COD ${item.code}</div></div><div class="catalog-meta"><span class="pill">${item.category}</span><span class="pill">${item.title}</span></div><div class="price-list"><div><strong>Precio 1:</strong> L ${item.price1}</div><div><strong>Precio 2:</strong> L ${item.price2}</div></div><div class="inline-actions"><a class="btn btn-secondary" href="editor-item.html?id=${encodeURIComponent(item.id)}">Editar</a><a class="btn btn-secondary" href="${item.image}" download="catalogo-${item.code}.jpg">Descargar</a></div></article>`;
}

function renderCatalog(target, items) {
  if (!target) return;
  if (!items.length) {
    target.innerHTML = `<div class="empty-state"><h3>No hay resultados</h3><p>Prueba otra busqueda o agrega una nueva prenda desde la version web.</p></div>`;
    return;
  }
  target.innerHTML = items.map(createProductCard).join("");
}

function initHomePage(items) {
  const categoryGrid = document.querySelector("[data-category-grid]");
  const featuredGrid = document.querySelector("[data-featured-grid]");
  if (categoryGrid) categoryGrid.innerHTML = CATEGORY_INFO.map(createCategoryCard).join("");
  if (featuredGrid) renderCatalog(featuredGrid, items.slice(0, 4));
  const totalItems = document.querySelector("[data-total-items]");
  const totalCategories = document.querySelector("[data-total-categories]");
  const totalEditable = document.querySelector("[data-total-editable]");
  if (totalItems) totalItems.textContent = String(items.length);
  if (totalCategories) totalCategories.textContent = String(CATEGORY_INFO.length);
  if (totalEditable) totalEditable.textContent = "100%";
}

function initCategoryPage(items, categoryName) {
  const input = document.querySelector("[data-search]");
  const grid = document.querySelector("[data-catalog-grid]");
  const count = document.querySelector("[data-results-count]");
  const info = getCategoryInfo(categoryName);
  const baseItems = items.filter((item) => item.category === categoryName);
  const update = () => {
    const query = input ? input.value.trim().toLowerCase() : "";
    const filtered = baseItems.filter((item) => item.code.toLowerCase().includes(query) || item.title.toLowerCase().includes(query) || buildSeo(item).includes(query));
    if (count) count.textContent = `${filtered.length} prenda${filtered.length === 1 ? "" : "s"} en ${info ? info.title : categoryName}`;
    renderCatalog(grid, filtered);
  };
  if (input) input.addEventListener("input", update);
  update();
}

function initSearchPage(items) {
  const input = document.querySelector("[data-search]");
  const grid = document.querySelector("[data-catalog-grid]");
  const count = document.querySelector("[data-results-count]");
  const update = () => {
    const query = input ? input.value.trim().toLowerCase() : "";
    const filtered = items.filter((item) => item.code.toLowerCase().includes(query) || item.title.toLowerCase().includes(query) || item.category.toLowerCase().includes(query) || buildSeo(item).includes(query));
    if (count) count.textContent = `${filtered.length} resultado${filtered.length === 1 ? "" : "s"} encontrados`;
    renderCatalog(grid, filtered);
  };
  if (input) input.addEventListener("input", update);
  update();
}

function fillCategoryOptions(select) {
  if (!select) return;
  select.innerHTML = `<option value="">Selecciona una categoria</option>${CATEGORY_INFO.map((item) => `<option value="${item.category}">${item.title}</option>`).join("")}`;
}

function initAddPage(items) {
  const form = document.querySelector("[data-add-form]");
  const imageInput = document.querySelector("[data-image-input]");
  const previewImage = document.querySelector("[data-preview-image]");
  const previewPlaceholder = document.querySelector("[data-preview-placeholder]");
  const previewCode = document.querySelector("[data-preview-code]");
  const codeInput = document.querySelector("[data-code]");
  const categorySelect = document.querySelector("[data-category-select]");
  const notice = document.querySelector("[data-notice]");
  fillCategoryOptions(categorySelect);
  if (codeInput) codeInput.value = nextCode(items);
  if (previewCode && codeInput) previewCode.textContent = `COD ${codeInput.value}`;
  if (imageInput) {
    imageInput.addEventListener("change", () => {
      const [file] = imageInput.files || [];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = () => {
        if (previewImage) {
          previewImage.src = String(reader.result);
          previewImage.classList.remove("hide");
        }
        if (previewPlaceholder) previewPlaceholder.classList.add("hide");
      };
      reader.readAsDataURL(file);
    });
  }
  if (!form) return;
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const code = String(formData.get("code") || "").trim();
    const category = String(formData.get("category") || "").trim();
    const price1 = String(formData.get("price1") || "").trim();
    const price2 = String(formData.get("price2") || "").trim();
    const title = String(formData.get("title") || "").trim() || `Prenda ${code}`;
    const image = previewImage && previewImage.getAttribute("src") ? previewImage.getAttribute("src") : "";
    if (!code || !category || !price1 || !price2 || !image) {
      if (notice) {
        notice.textContent = "Completa codigo, categoria, precios e imagen antes de guardar.";
        notice.classList.remove("hide");
      }
      return;
    }
    const catalog = getCatalog();
    catalog.push({ id: code, code, title, category, price1, price2, image });
    saveCatalog(catalog);
    if (notice) {
      notice.textContent = "Prenda guardada en este navegador. Ya puedes verla en la busqueda general.";
      notice.classList.remove("hide");
    }
    form.reset();
    if (previewImage) {
      previewImage.src = "";
      previewImage.classList.add("hide");
    }
    if (previewPlaceholder) previewPlaceholder.classList.remove("hide");
    const newCode = nextCode(getCatalog());
    if (codeInput) codeInput.value = newCode;
    if (previewCode) previewCode.textContent = `COD ${newCode}`;
  });
}

function initEditorPage(items) {
  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");
  const form = document.querySelector("[data-editor-form]");
  const deleteButton = document.querySelector("[data-delete]");
  const notice = document.querySelector("[data-notice]");
  const item = items.find((entry) => entry.id === id || entry.code === id);
  if (!item) {
    if (notice) {
      notice.textContent = "No se encontro la prenda solicitada. Usa la busqueda general para elegir una.";
      notice.classList.remove("hide");
    }
    if (form) form.classList.add("hide");
    return;
  }
  document.querySelector("[data-editor-image]").src = item.image;
  document.querySelector("[data-editor-title]").textContent = item.title;
  document.querySelector("[data-editor-meta]").textContent = `${item.category} · Codigo ${item.code}`;
  const fieldCode = document.querySelector("[data-editor-code]");
  const fieldCategory = document.querySelector("[data-editor-category]");
  const fieldPrice1 = document.querySelector("[data-editor-price1]");
  const fieldPrice2 = document.querySelector("[data-editor-price2]");
  fieldCode.value = item.code;
  fieldCategory.value = item.category;
  fieldPrice1.value = item.price1;
  fieldPrice2.value = item.price2;
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    const catalog = getCatalog();
    const index = catalog.findIndex((entry) => entry.id === item.id || entry.code === item.code);
    if (index === -1) return;
    catalog[index] = { ...catalog[index], code: fieldCode.value.trim(), id: fieldCode.value.trim(), category: fieldCategory.value.trim(), price1: fieldPrice1.value.trim(), price2: fieldPrice2.value.trim() };
    saveCatalog(catalog);
    if (notice) {
      notice.textContent = "Cambios guardados en el catalogo local.";
      notice.classList.remove("hide");
    }
  });
  deleteButton.addEventListener("click", () => {
    if (!window.confirm("Confirma que deseas eliminar esta prenda del catalogo local.")) return;
    saveCatalog(getCatalog().filter((entry) => !(entry.id === item.id || entry.code === item.code)));
    window.location.href = "busqueda-general.html";
  });
}

function initCropperPage() {
  const input = document.querySelector("[data-crop-input]");
  const image = document.querySelector("[data-crop-image]");
  const range = document.querySelector("[data-crop-range]");
  const value = document.querySelector("[data-crop-value]");
  if (input) {
    input.addEventListener("change", () => {
      const [file] = input.files || [];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = () => {
        image.src = String(reader.result);
        image.classList.remove("hide");
      };
      reader.readAsDataURL(file);
    });
  }
  if (range) {
    const update = () => {
      const zoom = Number(range.value);
      value.textContent = `${zoom}%`;
      if (image) image.style.transform = `scale(${zoom / 100})`;
    };
    range.addEventListener("input", update);
    update();
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const page = document.body.dataset.page || "";
  const category = document.body.dataset.category || "";
  const items = getCatalog();
  setActiveNav(page === "categoria" ? "busqueda" : page);
  if (page === "inicio") initHomePage(items);
  if (page === "busqueda") initSearchPage(items);
  if (page === "categoria") initCategoryPage(items, category);
  if (page === "agregar") initAddPage(items);
  if (page === "editor") initEditorPage(items);
  if (page === "cropper" || page === "mycrop") initCropperPage();
});
