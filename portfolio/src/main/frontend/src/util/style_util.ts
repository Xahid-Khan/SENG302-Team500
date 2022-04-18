export const mergeClassNames = (...classNames: string[]): string => {
    return classNames.filter(name => !!name).join(" ")
}