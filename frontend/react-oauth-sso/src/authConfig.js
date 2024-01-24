// OIDC Authentication Configuration
const authConfig = {
  // URL of the Identity Provider (IDP) or issuer
  authority: '', // Replace with your issuer URL

  // Unique identifier for the client (your application)
  client_id: '', // Replace with your client ID

  // Redirect URL where the IDP will send back tokens after successful user authentication
  redirect_uri: '',

  // Response type indicating the flow (Authorization Code Flow in this case)
  response_type: 'code',

  // Scopes define the access and permissions requested by your application
  scope: 'openid profile email',

  // URL to redirect the user after logout
  post_logout_redirect_uri: '',

  // URL where the application can request additional user information after authentication
  userinfo_endpoint: '', // Replace with your user-info endpoint

  // Mode in which the authorization response parameters are returned
  response_mode: 'query',

  // Code challenge method used for PKCE (Proof Key for Code Exchange)
  code_challenge_method: 'S256',
};

// Export the authentication configuration for use in other parts of the application
export default authConfig;
