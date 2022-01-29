import { apiRequest, apiEndpoints, apiRequestRaw } from '../apiclient';
import { JwtAuthenticationDto } from '../types/generated';

export function login({ loginState, email, password, handleLoginFailure }: { loginState: any, email: string, password: string, handleLoginFailure: Function }): void {
    apiRequestRaw<JwtAuthenticationDto>({
        apiEndpoint: apiEndpoints.login.login,
        authToken: loginState.accessToken,
        data: { email, password },
    })
        .then(result => {
            loginState.accessTokenChanged(result.response?.accessToken);
        })
        .catch(err => {
            if (handleLoginFailure) {
                handleLoginFailure(err);
            }
        });
}

export function logout({ loginState }): void {
    loginState.accessTokenChanged(null);
}

export function requestPasswordReset({ email }): Promise<void> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.login.pwreset,
        data: { email }
    }).then(result => undefined);
}
