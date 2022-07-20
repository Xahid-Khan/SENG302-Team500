export const polyfill = () => {
    console.log("Polyfilling!")
    if ((window as any).global === undefined) {
        (window as any).global = window
    }
}

polyfill()