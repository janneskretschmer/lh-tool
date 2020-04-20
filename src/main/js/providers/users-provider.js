import { createUser } from "../actions/user";

export const UsersContext = React.createContext();

@withContext('sessionState', SessionContext)
export default class UsersProvider extends React.Component {

    state = {
        users: [],
    }

    addUser({ email, firstName, lastName, gender, telephoneNumber, mobileNumber, businessNumber, skills, profession, role }, handleFailure) {
        createUser({ email, firstName, lastName, gender, telephoneNumber, mobileNumber, businessNumber, skills, profession, role }, handleFailure).then(
            user =>
                new Promise(resolve => this.setState(prevState => ({
                    users: [...prevState.users, user],
                }), resolve))
        )
    }

    fetchUsersByProjectIdAndRole(projectId, role) {

    }
}