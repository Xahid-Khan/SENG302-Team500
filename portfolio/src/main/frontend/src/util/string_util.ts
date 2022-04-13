export const leftPadNumber = (number: number, places: number) => {
    const numberString = `${number}`

    if (numberString.length >= places) {
        return numberString;
    }

    return ('0'.repeat(places - numberString.length)) + number;
}