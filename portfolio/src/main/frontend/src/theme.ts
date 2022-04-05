import {blue, orange, red} from '@mui/material/colors'
import {createTheme} from "@mui/material"

const theme = createTheme({
    palette: {
        primary: {
            main: blue.A400
        },
        secondary: {
            main: orange.A400
        },
        error: {
            main: red.A400
        }
    }
})

export default theme