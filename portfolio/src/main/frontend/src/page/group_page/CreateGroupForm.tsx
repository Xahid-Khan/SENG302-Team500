import * as React from "react";
import {FormEvent} from "react";


export function CreateGroupForm() {

    const [shortName, setShortName] = React.useState('')
    const [shortCharCount, setShortCharCount] = React.useState(0)

    const [longName, setLongName] = React.useState('')
    const [longCharCount, setLongCharCount] = React.useState(0)

    const validateCreateForm = async (e: FormEvent) => {
        let errors = false
        let errorMessage

        if (shortCharCount === 0) {
            errors = true
            errorMessage = "Please enter a short name for the new group"
        }

        if (!errors && longCharCount === 0) {
            errors = true
            errorMessage = "Please enter a long name for the new group"
        }

        if (errors) {
            e.preventDefault()
            document.getElementById("create-group-error").innerText = errorMessage;
        } else {
            const response = await fetch(`api/v1/groups`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify([longName, shortName])
            });
        }
    }

    return (
        <form onSubmit={(e) => validateCreateForm(e)}>
            <div>
                <label>Short Name*:</label>
                <input type="text" name="short-name" className="input-name" id={"short-name"} maxLength={32} onChange={(e) => {setShortName(e.target.value); setShortCharCount(e.target.value.length)}}/>
                <span className="input-length" id="short-name-length">{shortCharCount} / 32</span>
            </div>
            <div>
                <label>Long Name*:</label>
                <input type="text" name="long-name" className="input-name" id={"long-name"} maxLength={64} onChange={(e) => {setLongName(e.target.value); setLongCharCount(e.target.value.length)}}/>
                <span className="input-length" id="long-name-length">{longCharCount} / 64</span>
            </div>
            <div className="form-error" id="create-group-error"/>
            <p>* = Required field.</p>
            <div className={"modal-buttons"}>
                <button className={"button"} type={"submit"} id={"modal-create-group-confirm"}>Save</button>
                <button className={"button"} id={"modal-create-group-cancel"}
                        onClick={() => document.getElementById("modal-create-group-open").style.display = "none"}>Cancel
                </button>
            </div>
        </form>
    );
}