import { apiRequest, apiEndpoints } from '../apiclient';

export function login({ loginState, email, password }) {
    apiRequest({
        apiEndpoint: apiEndpoints.login.login,
        data: { email, password },
    })
        .then(result => {
            loginState.accessTokenChanged(result.response.accessToken);
        })
        .catch(err => {
            // TODO Prettier error message
            alert('Login fehlgeschlagen');
        })
}

export function logout({ loginState }) {
    loginState.accessTokenChanged(null);
}
