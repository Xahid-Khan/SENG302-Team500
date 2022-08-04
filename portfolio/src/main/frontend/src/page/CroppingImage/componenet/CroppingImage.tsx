import Croppie, {CroppieOptions} from "croppie";
import React from "react";
import "croppie/croppie.css";

const croppieOptions : CroppieOptions = {
    showZoomer: true,
    enableOrientation: true,
    mouseWheelZoom: "ctrl",
    viewport: {
        width: 300,
        height: 300,
        type: "square"
    },
    boundary: {
        width: 350,
        height: 300
    },
};

const croppieElement = document.getElementById("croppie");
const croppie = new Croppie(croppieElement, croppieOptions);


class CroppingImage extends React.Component {
    state = {
        croppedImage : "",
        isFileUploaded: false,
        currentImage: ""
    };

    file: any = React.createRef();
    // croppie = React.createRef();
    img: any = React.createRef();

    checkCurrentImage = async () => {
        const currentImage = document.getElementById("userProfileImage")
        console.log("I'm here");
        console.log(currentImage.getAttribute("src"));


        if (currentImage.hasAttribute("hidden")) {
            console.log(currentImage.getAttribute("src"));
            console.log(currentImage.dataset);
        }
    };

    onFileUpload = () => {
        this.setState({ isFileUploaded: true }, () => {
            const reader = new FileReader();
            const file = this.file.current.files[0];
            reader.readAsDataURL(file);
            reader.onload = () => {
                croppie.bind({ url: ""+reader.result });
            };
            reader.onerror = function(error) {
                console.log("Error: ", error);
            };
        });
    };

    onResult = () => {
        croppie.result({type:"base64"}).then(base64 => {
            this.setState(
                { croppedImage: base64 },
                () => (this.img.current.src = base64),
            );
            document.getElementById("userProfileImage").setAttribute("src", base64);

        });
    };

    render() {
        const { isFileUploaded, croppedImage } = this.state;
        this.checkCurrentImage();
        return (
            <div id={"specialButtons"}>
                <input
                    type="file"
                    id="newUploadedImage"
                    ref={this.file}
                    onChange={this.onFileUpload}
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
                <hr style={{marginTop:"25px"}} />
                    <img ref={this.img} hidden={true}/>
            </div>
        );
    }
}

export {
    CroppingImage
}