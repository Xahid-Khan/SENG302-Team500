import React from "react";
import {CroppingImage} from "./componenet/CroppingImage";


export const CroppingUserImage: React.FC = () => {
    return (
        <div>
            <h1>Edit/Remove User Profile Photo</h1>
            <div>
                <CroppingImage/>
            </div>
        </div>
    )
}