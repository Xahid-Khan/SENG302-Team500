import Croppie, {CroppieOptions} from "croppie";
import React from "react";
import "croppie/croppie.css";
import ReactDOM from "react-dom";


export const CroppingImage: React.FC = () => {
    const [croppedImage, setCroppedImage] = React.useState(null);
    const [isImageUploaded, setIsImageUploaded] = React.useState(false);
    let imageData = React.useRef<any>();
    let file = React.useRef<any>();

    const croppieOpts: CroppieOptions = {
        viewport: {
            width: 250,
            height: 250,
            type: "square"
        }
    };

    const placement = document.getElementById("croppie");
    const croppie = new Croppie(placement, croppieOpts);


    // let croppie = createRef();

    const onImageUpload = (event: any) => {
        setIsImageUploaded(true),
        () => {
            const reader = new FileReader();
            file = event.file.current.files[0];
            reader.readAsDataURL(file as any);
            reader.onload = () => {
                croppie.bind({url : reader.result.toString()});
            };
            reader.onerror = function (error) {
                console.log(error);
            }
        }
    }

    const onCroppingImage = (event: any) => {
        croppie.result({type: "base64"}).then(croppedFile => {
            setCroppedImage({userImage: croppedFile}),
                () => imageData.current.src = croppedFile;
        })
    }

    return (
        <div>
            <input
                type="file"
                id="files"
                ref={file}
                onChange={onImageUpload}
            />
            <hr />
            <button
                type="button"
                disabled={!isImageUploaded}
                onClick={onCroppingImage}
            >
                Crop!
            </button>
            <hr />
            <h2> Result! </h2>
            <div>
                <img ref={imageData} alt="cropped image" />
                <a hidden={!croppedImage} href={croppedImage} download="cropped.png">
                    Download Cropped Image
                </a>
            </div>
        </div>
    )
}

// const rootElement = document.getElementById("root");
// ReactDOM.render(<CroppingImage/>, rootElement)