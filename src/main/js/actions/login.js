import { apiRequest, apiEndpoints } from '../apiclient';

export function login({ loginState, email, password, handleLoginFailure }) {
    apiRequest({
        apiEndpoint: apiEndpoints.login.login,
        authToken: loginState.accessToken,
        data: { email, password },
    })
        .then(result => {
            loginState.accessTokenChanged(result.response.accessToken);
        })
        .catch(err => {
            if (handleLoginFailure) {
                handleLoginFailure(err);
            }
        });
}

export function logout({ loginState }) {
    loginState.accessTokenChanged(null);
}

export function requestPasswordReset({ email }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.login.pwreset,
        data: { email }
    });
}
