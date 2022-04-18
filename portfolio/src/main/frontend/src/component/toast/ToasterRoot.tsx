import React, {useState} from "react";
import {ToasterStoreProvider, useToasterStore} from "./internal/ToasterStoreProvider";
import {ToasterStore} from "./internal/ToasterStore";
import {Toaster} from "./internal/Toaster";

export const ToasterRoot: React.FC = ({ children }) => {
    const [store, _] = useState(() => new ToasterStore())

    return (
        <ToasterStoreProvider value={store}>
            <Toaster/>
            {children}
        </ToasterStoreProvider>
    )
}