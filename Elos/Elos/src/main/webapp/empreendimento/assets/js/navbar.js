const body = document.querySelector("body");
const darkLight = document.querySelector("#darkLight");
const sidebar = document.querySelector(".sidebar");
const submenuItems = document.querySelectorAll(".submenu_item");
const sidebarOpen = document.querySelector("#sidebarOpen");
const sidebarClose = document.querySelector(".collapse_sidebar");
const sidebarExpand = document.querySelector(".expand_sidebar");

// --- Mantenha o submenu ativo ao recarregar a página ---
const getActiveSubmenu = localStorage.getItem("activeSubmenu");
if (getActiveSubmenu) {
    const activeSubmenuElement = document.querySelector(`.submenu_item[data-submenu="${getActiveSubmenu}"]`);
    if (activeSubmenuElement) {
        activeSubmenuElement.classList.add("show_submenu");
    }
}

// === Restaurar modo dark/light salvo === 
let savedMode = localStorage.getItem("mode");
if (savedMode === "dark") {
    body.classList.add("dark");
    darkLight.classList.replace("bx-sun", "bx-moon");
} else {
    darkLight.classList.replace("bx-moon", "bx-sun");
}

// === Restaurar estado do menu lateral salvo === 
let savedSidebar = localStorage.getItem("sidebarStatus");
if (savedSidebar === "close") {
    sidebar.classList.add("close");
    sidebar.classList.add("hoverable");
    body.classList.add("sidebar-closed");
} else {
    sidebar.classList.remove("close");
    sidebar.classList.remove("hoverable");
    body.classList.remove("sidebar-closed");
}

// === Dark mode toggle === 
darkLight.addEventListener("click", () => {
    body.classList.toggle("dark");
    if (body.classList.contains("dark")) {
        darkLight.classList.replace("bx-sun", "bx-moon");
        localStorage.setItem("mode", "dark");
    } else {
        darkLight.classList.replace("bx-moon", "bx-sun");
        localStorage.setItem("mode", "light");
    }
});

// === Sidebar toggle === 
sidebarOpen.addEventListener("click", () => {
    sidebar.classList.toggle("close");
    if (sidebar.classList.contains("close")) {
        localStorage.setItem("sidebarStatus", "close");
    } else {
        localStorage.setItem("sidebarStatus", "open");
    }
});

sidebarClose.addEventListener("click", () => {
    sidebar.classList.add("close");
    sidebar.classList.add("hoverable");
    body.classList.add("sidebar-closed");
    body.classList.remove("sidebar-hovered");
    localStorage.setItem("sidebarStatus", "close");
});

sidebarExpand.addEventListener("click", () => {
    sidebar.classList.remove("close");
    sidebar.classList.remove("hoverable");
    body.classList.remove("sidebar-closed");
    body.classList.remove("sidebar-hovered");
    localStorage.setItem("sidebarStatus", "open");
});

// === Sidebar hover behavior === 
sidebar.addEventListener("mouseenter", () => {
    if (sidebar.classList.contains("hoverable")) {
        sidebar.classList.remove("close");
        body.classList.add("sidebar-hovered");
    }
});

sidebar.addEventListener("mouseleave", () => {
    if (sidebar.classList.contains("hoverable")) {
        sidebar.classList.add("close");
        body.classList.remove("sidebar-hovered");
    }
});

// === Submenus === 
submenuItems.forEach((item, index) => {
    item.addEventListener("click", () => {
        const submenuId = item.getAttribute('data-submenu');

        if (item.classList.contains("show_submenu")) {
            item.classList.remove("show_submenu");
            localStorage.removeItem("activeSubmenu");
        } else {
            submenuItems.forEach((item2) => {
                item2.classList.remove("show_submenu");
            });

            item.classList.add("show_submenu");
            localStorage.setItem("activeSubmenu", submenuId);
        }
    });
});

// === Mobile sidebar behavior === 
if (window.innerWidth < 768) {
    sidebar.classList.add("close");
} else {
    if (savedSidebar !== "close") {
        sidebar.classList.remove("close");
    }
}