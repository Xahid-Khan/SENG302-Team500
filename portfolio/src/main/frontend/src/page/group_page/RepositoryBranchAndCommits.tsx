import * as React from "react";

const branchRequestWithParams = async (repositoryId: number, token: string) => {
    const request = new Request(`https://eng-git.canterbury.ac.nz/api/v4/projects/${repositoryId}/repository/branches`, {
        method: 'GET',
        headers: new Headers({
            'PRIVATE-TOKEN': token,
        })
    })
    return request;
}
const commitRequestWithParams = async (repositoryId: number, token: string) => {
    const request = new Request(`https://eng-git.canterbury.ac.nz/api/v4/projects/${repositoryId}/repository/commits`, {
        method: 'GET',
        headers: new Headers({
            'PRIVATE-TOKEN': token,
        })
    })
    return request;
}
export const getBranchAndCommit = async (groupId: number, repositoryId: number, token: string) => {
    const commitResponse = await fetch(await commitRequestWithParams(repositoryId, token))
    const branchResponse = await fetch(await branchRequestWithParams(repositoryId, token))
    const commits = await commitResponse.json();
    const branches = await branchResponse.json();
    const groupRepositoryInfo = {
        id: groupId,
        commits: commits,
        branches: branches
    }
    return groupRepositoryInfo;
}



export function repositoryBranchAndCommits(groupId: any) {
    const[repositoryId, setRepositoryId] = React.useState(0);
    const[token, setToken] = React.useState('');
    const[commits, setCommits] = React.useState([])
    const[branches, setBranches] = React.useState([])
    const[loading, setLoading] = React.useState(true)
    const[error, setError] = React.useState(false)
    const getRepositoryAndToken = async () => {
        const response = await fetch(`/groups/repository/${groupId}/`)
        const data = await response.json();
        setRepositoryId(data.repositoryId);
        setToken(data.token);
    }

    getRepositoryAndToken().then(() => {
        getBranchAndCommit(groupId, repositoryId, token).then((data) => {
            setCommits(data.commits);
            setBranches(data.branches);
            setLoading(false);
        }).catch(() => {
            setError(true);
            setLoading(false);
        })
    })
    // React.useEffect(() => {
    //     getRepositoryAndToken().then(() => {
    //         getBranchAndCommit(groupId, repositoryId, token).then((data) => {
    //             setCommits(data.commits);
    //             setBranches(data.branches);
    //             setLoading(false);
    //         }).catch(() => {
    //             setError(true);
    //             setLoading(false);
    //         })
    //     })
    //     getBranchAndCommit(groupId, repositoryId, token).then((groupRepositoryInfo) => {
    //         setCommits(groupRepositoryInfo.commits)
    //         setBranches(groupRepositoryInfo.branches)
    //         setLoading(false)
    //     }).catch(() => {
    //         setError(true)
    //     })
    // }, [])

    return <div className={"groups-page-repository"} id={`groups-repository`}>
        <h3 className={'group-repository-title'}>Branches</h3>
        <div className={"table"} id={"group-list-branches"}>

            {/*Run getbranches for each group*/}
            {/*find allGroupsBranchCommits which contains the same group id then map the branch*/}
            {branches.map((branch: any) => (
                <div className="tableRow">
                    <div className="tableCell">
                        <a href={branch['web_url']}
                           target="_blank">{branch['name']} ({Object.keys(branch['commit']).length} commits)</a>
                        <br></br>
                    </div>
                </div>
            ))}
        </div>
        <h3 className={'group-repository-title'}>Commits</h3>
        <div className={"table"} id={"group-list-commits"}>

            {commits.map((commit: any) => (
                <div className="tableRow">
                    <div className="tableCell">
                        <strong>Name:</strong>{commit['author_name']} <br></br>
                        <strong>Message:</strong> {commit['message']} <br></br>
                        <strong>ID:</strong><a href={commit['web_url']} target="_blank">{commit['id']}</a> <br></br>
                    </div>
                </div>
            ))}
        </div>
    </div>;
}