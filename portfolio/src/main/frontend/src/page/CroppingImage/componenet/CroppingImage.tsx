import Croppie, {CroppieOptions} from "croppie";
import React from "react";
import "croppie/croppie.css";

const croppieOptions : CroppieOptions = {
    showZoomer: true,
    enableOrientation: true,
    mouseWheelZoom: "ctrl",
    viewport: {
        width: 250,
        height: 250,
        type: "square"
    },
    boundary: {
        width: 300,
        height: 300
    },
};

console.log("I'm here...")

const CheckFileTypeAndSize = (file : any) : boolean => {
    const errorElement = document.getElementById("image-preview-error");
    const warningElement = document.getElementById("image-preview-warning")

    if (!(file.type == "image/jpeg" || file.type == "image/png" || file.type == "image/gif")) {
        errorElement.innerHTML = "Invalid File Type - Only JPEG, PNG, and GIF are allowed.";
        document.getElementById("image-preview-error").removeAttribute("hidden");
        document.getElementById('submit').setAttribute('disabled', "true");
        warningElement.setAttribute("hidden", "true");
        return false
    }

    if (file.size > 10 * 1024 * 1024) {

        errorElement.innerHTML = "Image size is too big - 10MB max is allowed";
        warningElement.setAttribute("hidden", "true");
        document.getElementById("image-preview-error").removeAttribute("hidden");
        document.getElementById('submit').setAttribute('disabled', "true");
        return false
    }

    if (file.size > 5 * 1024 * 1024) {
        errorElement.setAttribute("hidden", "true");
        warningElement.removeAttribute("hidden")
        warningElement.innerHTML = "Your file is greater than 5MB - it will be compressed to save space.";
        document.getElementById('submit').setAttribute('disabled', "true");
    }

    errorElement.setAttribute("hidden", "true");
    return true;
}

const croppieElement = document.getElementById("croppie");
const croppie = new Croppie(croppieElement, croppieOptions);

class CroppingImage extends React.Component {
    state = {
        croppedImage : "",
        isFileUploaded: false,
    };

    file: any = React.createRef();
    img: any = React.createRef();

    onFileUpload = () => {
        this.setState({ isFileUploaded: true }, () => {
            const reader = new FileReader();
            const file = this.file.current.files[0];

            if (CheckFileTypeAndSize(file)) {
                reader.readAsDataURL(file);

                reader.onload = () => {
                    croppie.bind({url: "" + reader.result});
                };
                reader.onerror = function (error) {
                    console.log("Error: ", error);
                };
            }
        });
    };

    onResult = () => {
        croppie.result({type:"base64"}).then(base64 => {
            this.setState(
                { croppedImage: base64 },
                () => (this.img.current.src = base64),
            );
            document.getElementById('submit').removeAttribute('disabled');
            document.getElementById('previewText').setAttribute("hidden", "true");
            document.getElementById("userProfileImage").setAttribute("src", base64);
            document.getElementById("userProfileImage").removeAttribute('hidden');

        });
    };

    render() {
        const { isFileUploaded, croppedImage } = this.state;

        document.getElementById("newCroppedUserImage").setAttribute("value", croppedImage);
        return (
            <div id={"specialButtons"} style={{display:'block'}}>
                <input
                    type="file"
                    id="newUploadedImage"
                    ref={this.file}
                    onChange={this.onFileUpload}
                    name={"image"}
                    accept="image/jpeg,image/png,image/gif" hidden
                />

                <label htmlFor="newUploadedImage">
                    <span className="image-upload-button">Choose profile picture</span>
                </label>

                <button
                    type="button"
                    disabled={!isFileUploaded}
                    onClick={this.onResult}
                    style={{float:"right", marginTop:"-30px"}}
                >
                    Crop Image
                </button>
                <hr style={{marginTop:"25px"}}/>
                    <img ref={this.img} hidden={true}/>
            </div>
        );
    }
}


export {
    CroppingImage
}