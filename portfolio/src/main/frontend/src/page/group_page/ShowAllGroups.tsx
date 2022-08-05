import * as React from "react";


const getGroups = () => {
    return [{
        "longName": "Teaching Team",
        "shortName": "Teachers",
        "users": [
                {
                    "id": "1",
                    "name": "John",
                    "username": "johnstewart",
                    "alias": "Johnny",
                    "roles": ["Teacher, Student"]
                },
                {
                    "id": "2",
                    "name": "Sarah",
                    "username": "sarahjohn",
                    "alias": "Sa",
                    "roles": ["Student"]
                }
            ]
    }]
}



export function ShowAllGroups() {

    const groups = getGroups()

    return (
        <div>
            {groups.map((group: any) => (
                <div className={'raised-card group'}>
                    <div className={'group-header'}>
                        <h2 className={'group-name-long'}>{group['longName']}</h2>
                        <button className="button edit-group-button" id="edit-group" data-privilege="teacher"> Edit
                            Group
                        </button>


                    </div>
                    <h3 className={'group-name-short'}>{group['shortName']}</h3>
                    <div className={"table"}>
                        <div className={"groups-table-header"}>
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