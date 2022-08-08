import * as React from "react";


const getGroups = () => {
    return [{
        "id": 1,
        "longName": "Teaching Team",
        "shortName": "Teachers",
        "users": [
                {
                    "id": 1,
                    "name": "John",
                    "username": "johnstewart",
                    "alias": "Johnny",
                    "roles": ["Teacher, Student"]
                },
                {
                    "id": 2,
                    "name": "Sarah",
                    "username": "sarahjohn",
                    "alias": "Sa",
                    "roles": ["Student"]
                }
            ]
    }]
}



export function ShowAllGroups({setViewGroupId}: any) {

    const groups = getGroups()

    return (
        <div>
            {groups.map((group: any) => (
                <div className={'raised-card group'}>
                    <h2 className={'group-name-long'}>{group['longName']}</h2>
                    <h3 className={'group-name-short'}>{group['shortName']}</h3>
                    <button className={"button"} onClick={() => {
                        document.getElementById("group-settings-modal-open").style.display='block';
                        setViewGroupId(group.id)
                    }}><span className={"material-icons"}>settings</span> </button>
                    <div className={"table"}>
                        <div className={"groups-header"}>
                            <div className="tableCell"><b>Name</b></div>
                            <div className="tableCell"><b>Username</b></div>
                            <div className="tableCell"><b>Alias</b></div>
                            <div className="tableCell"><b>Roles</b></div>
                        </div>
                        {group['users'].map((user: any) => (
                            <div className="tableRow">
                                <div className="tableCell">{user['name']}</div>
                                <div className="tableCell">{user['username']}</div>
                                <div className="tableCell">{user['alias']}</div>
                                <div className="tableCell">{user['roles'].toString()}</div>
                            </div>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
}