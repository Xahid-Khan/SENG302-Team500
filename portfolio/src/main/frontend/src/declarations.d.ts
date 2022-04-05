// Thanks to: https://stackoverflow.com/a/41946697
declare module '*.scss' {
    const content: Record<string, string>;
    export default content;
}