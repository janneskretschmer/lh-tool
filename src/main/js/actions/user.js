import { apiRequest, apiEndpoints } from '../apiclient';

export function fetchCurrentUser({ accessToken }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.current,
        authToken: accessToken,
    })
        .then(result => result.response)
        .catch(err => null);
}

export function changePassword({ userId, token, oldPassword, newPassword, confirmPassword }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.password,
        data: { userId, token, oldPassword, newPassword, confirmPassword }
    })
        .then(result => result.response);
}
