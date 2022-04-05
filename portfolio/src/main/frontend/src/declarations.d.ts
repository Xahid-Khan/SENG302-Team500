// Thanks to: https://stackoverflow.com/a/41946697
declare module '*.module.css' {
    const content: Record<string, string>;
    export default content;
}