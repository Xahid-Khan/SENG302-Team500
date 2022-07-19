import {leftPadNumber} from "./string_util";

/**
 * Utilities to make it easier to work with Dates.
 */
export class DatetimeUtils {
    /**
     * Convert a network datetime string (in UTC) to a JavaScript Date (in the local timezone).
     */
    static networkStringToLocalDate(utcString: string) {
        return new Date(Date.parse(utcString))
    }

    /**
     * Convert a JavaScript Date (in local timezone) to a network datetime string (in UTC)
     */
    static localToNetworkString(localDate: Date) {
        return localDate.toISOString();
    }

    /**
     * Convert a JavaScript Date (in local timezone) to a network datetime string (in local timezone)
     */
    static localToNetworkStringWithTimezone(localDate: Date) {
        let offsetDate = new Date(localDate)
        offsetDate.setHours(offsetDate.getHours() - (offsetDate.getTimezoneOffset() / 60)) // Offsets date to nullify offset caused by toISOString(), leaving date in local time
        return offsetDate.toISOString();
    }

    /**
     * Round a JavaScript Date (possibly including a time component) to the start of the day *in the local timezone*
     * and format it to a string of the format 'yyyy-mm-dd'
     */
    static toLocalYMD(localDate: Date) {
        return `${leftPadNumber(localDate.getFullYear(), 4)}-${leftPadNumber(localDate.getMonth() + 1, 2)}-${leftPadNumber(localDate.getDate(), 2)}`
    }

    /**
     * Parse a JavaScript Date (in the local timezone) from a string of the format 'yyyy-mm-dd'.
     *
     * Note that the string is assumed to be valid. No validation is done in this method.
     */
    static fromLocalYMD(localString: string) {
        // From: https://stackoverflow.com/a/64199706
        const [year, month, day] = localString.split('-');
        return new Date(parseInt(year, 10), parseInt(month, 10) - 1, parseInt(day, 10));
    }

    /**
     * Returns whethor or not that given JavaScript Date has a time component.
     */
    static hasTimeComponent(date: Date) {
        return date.getHours() !== 0 || date.getMinutes() !== 0 || date.getSeconds() !== 0
    }

    /**
     * If the given date is at time 00:00:00 in the local timezone, returns null.
     *
     * Otherwise, will return a 'HH:MM[:SS]'-formatted string.
     */
    static getTimeStringIfNonZeroLocally(date: Date) {
        if (this.hasTimeComponent(date)) {
            // There is an hours/minutes/seconds component to this date in the local timezone.
            return `${date.getHours()}:${leftPadNumber(date.getMinutes(), 2)}${(date.getSeconds() !== 0) ? ':' + leftPadNumber(date.getSeconds(), 2) : ''}`;
        }
        return null;
    }

    /**
     * Converts the given Date into an HH:MM string in the local timezone.
     */
    static toLocalHM(date: Date) {
        return `${date.getHours()}:${leftPadNumber(date.getMinutes(), 2)}`
    }

    /**
     * Copies the given date, setting the hours and minutes component to match the hours and minutes parsed from the
     * hm string.
     *
     * @return Date with the new hours and minutes or null if hm is of an invalid format.
     */
    static withLocalHM(date: Date, hm: string): Date | null {
        const [hoursStr, minutesStr] = hm.split(/:/, 2)
        const hours = parseInt(hoursStr, 10)
        const minutes = parseInt(minutesStr, 10)
        if (isNaN(hours) || isNaN(minutes)) {
            return null
        }
        const newDate = new Date(date.getTime())
        newDate.setHours(hours)
        newDate.setMinutes(minutes)
        return newDate
    }

    /**
     * Format a JavaScript Date to a date string suitable for presentation to the user.
     *
     * Note that if the given Date has a non-zero time component in the local timezone the output of {@link getTimeStringIfNonZeroLocally} may be appended.
     */
    static localToUserDMY(localDate: Date) {
        const hoursComponent = this.getTimeStringIfNonZeroLocally(localDate);
        return `${localDate.getDate()} ${localDate.toLocaleString('default', {month: 'long'})} ${localDate.getFullYear()}${(hoursComponent !== null) ? ' ' + hoursComponent : ''}`;
    }

    /**
     * Checks whether the given dates are equal.
     */
    static areEqual(date1: Date, date2: Date) {
        return date1 <= date2 && date2 <= date1;
    }


    /**
     * Sets time to zero to check for all related events as it is only relevant for things that occur on the same day rather than the same time
     * Setting time to zero ensures that dates will be equal and not offset by time
    */
    static setTimeToZero(fullDate: Date) {
        return new Date(fullDate.getFullYear(), fullDate.getMonth(), fullDate.getDate()).getTime()
    }
}