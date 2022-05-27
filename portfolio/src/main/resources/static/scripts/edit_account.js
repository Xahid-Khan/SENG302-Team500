const MAX_FILE_SIZE = 10 * 1024 * 1024;
const MAX_UNCOMPRESSED_FILE_SIZE = 5 * 1024 * 1024;
const IMAGE_SIZE = 150;

(() => {
    let blockFormSubmit = false;

    const formEl = document.getElementById("form");
    const submitBtnEl = document.getElementById("submit");
    const inputEl = document.getElementById("newUploadedImage");
    const warningEl = document.getElementById("image-preview-warning");
    const errorEl = document.getElementById("image-preview-error");
    const imagePreviewEl = document.getElementById("image-preview");

    let fetchPreviewController = null;

    formEl.addEventListener('submit', (evt) => {
        if (blockFormSubmit) {
            evt.preventDefault();
            alert("Please choose a valid picture before proceeding.");
        }
    })

    const showWarning = (message) => {
        warningEl.style.display = 'block';
        warningEl.innerText = message;
    }

    const hideWarning = () => {
        warningEl.style.display = 'none';
    }

    const showError = (message) => {
        errorEl.style.display = 'block';
        errorEl.innerText = message;
        showIllegalImage();

        blockFormSubmit = true;
        submitBtnEl.setAttribute("disabled", true);
    }

    const hideErrors = () => {
        errorEl.style.display = 'none';

        blockFormSubmit = false;
        submitBtnEl.removeAttribute("disabled");
    }

    const hideImagePreview = () => {
        imagePreviewEl.style.display = "none"
    }

    const showImagePreview = async (imageData) => {
        const image = await createImageBitmap(imageData, {
            resizeWidth: IMAGE_SIZE,
            resizeHeight: IMAGE_SIZE
        });

        if (fetchPreviewController.signal.aborted) {
            return;
        }

        const ctx = imagePreviewEl.getContext('2d');
        ctx.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        ctx.drawImage(image, 0, 0);
    }

    const showLoadingImage = () => {
        const ctx = imagePreviewEl.getContext('2d');
        ctx.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

        ctx.fillStyle = "black";
        ctx.font = "bold 14px sans-serif";
        ctx.textAlign = "center";
        ctx.fillText("Loading preview...", IMAGE_SIZE / 2, IMAGE_SIZE / 2);
    }

    const showIllegalImage = () => {
        const ctx = imagePreviewEl.getContext('2d');

        ctx.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

        ctx.strokeStyle = 'red';
        ctx.lineWidth = 3;

        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(IMAGE_SIZE, IMAGE_SIZE);
        ctx.stroke();
        ctx.beginPath();
        ctx.moveTo(0, IMAGE_SIZE);
        ctx.lineTo(IMAGE_SIZE, 0);
        ctx.stroke();
    }

    const fetchAndDisplayPreview = async () => {
        hideWarning();

        if (!inputEl.files || inputEl.files.length === 0) {
            return;
        }
        else if (inputEl.files[0].size === 0) {
            showError("Please select a file with content.");
        }
        else if (inputEl.files[0].size > MAX_FILE_SIZE) {
            showError("This file is too large. Please select a file of at most 10MB in size.");
        }
        else {
            if (inputEl.files[0].size > MAX_UNCOMPRESSED_FILE_SIZE) {
                showWarning("This file will be compressed. To avoid compression please choose a file less than 5MB in size.")
            }

            // Let's try to generate a preview...
            try {
                const formData = new FormData();
                formData.append("image", inputEl.files[0], inputEl.files[0].name);

                fetchPreviewController = new AbortController();

                const res = await fetch("edit_account/preview_picture", {
                    method: 'POST',
                    body: formData,
                    signal: fetchPreviewController.signal
                });

                if (fetchPreviewController.signal.aborted) {
                    return;
                }

                if (!res.ok) {
                    if (res.status === 400) {
                        try {
                            showError(await res.text());
                        }
                        catch (e) {
                            showError(`An unexpected error occurred while generating a preview.`);
                            console.error(e);
                        }
                    }
                    else {
                        showError(`An unexpected error occurred while generating a preview.`);
                        console.warn(res);
                    }
                }
                else {
                    // We're OK!
                    const data = await res.blob();

                    if (fetchPreviewController.signal.aborted) {
                        return;
                    }

                    try {
                        await showImagePreview(data);
                        hideErrors();
                    }
                    catch (e) {
                        hideImagePreview();
                        showError(`An unexpected error occurred while generating a preview.`);
                        console.error(e);
                    }
                }
            }
            catch (e) {
                if (e instanceof DOMException && e.name === 'AbortError') {
                    console.log("Aborted fetch.")
                }
                else {
                    hideImagePreview();
                    showError(`An unexpected error occurred while generating a preview.`)
                    console.error(e);
                }
            }
        }
    }

    const cancelCurrentPreviewFetchIfNeccessary = () => {
        if (fetchPreviewController !== null) {
            fetchPreviewController.abort();
        }
    }

    inputEl.addEventListener('change', () => {
        // File selected
        imagePreviewEl.style.display = "inline-block";

        cancelCurrentPreviewFetchIfNeccessary();
        showLoadingImage();
        fetchAndDisplayPreview();
    })
})();