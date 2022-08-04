import Croppie from "croppie";

const testCroppie = new Croppie(document.getElementById("croppieTestId"), {
    viewport: { width: 100, height: 100 },
    boundary: { width: 300, height: 300 },
    showZoomer: false,
    enableResize: true,
    enableOrientation: true,
    mouseWheelZoom: 'ctrl'
})();

