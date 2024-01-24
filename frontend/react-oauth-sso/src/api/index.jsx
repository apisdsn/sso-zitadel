// Importing Axios library for making HTTP requests
import axios from 'axios';

// Function to retrieve the stored access token from localStorage
const getStoredToken = async () => {
  // Key under which the access token is stored in localStorage
  const storedTokenKey =
    '';
  // Retrieve the stored token from localStorage
  const storedToken = JSON.parse(localStorage.getItem(storedTokenKey));

  // Check if the token or access_token is missing
  if (!storedToken || !storedToken.access_token) {
    console.error('Token not found. Please log in first.');
    return null;
  }

  // Return the access_token if available
  return storedToken.access_token;
};

// Call the getStoredToken function asynchronously to get the token
const token = await getStoredToken();
console.log('Token from async: ', token);

// Create an Axios instance for making API requests with the retrieved token
const Api = axios.create({
  // Base URL for the API requests
  baseURL: '',
  // Headers including the Authorization header with the Bearer token
  headers: {
    Authorization: `Bearer ${token}`,
    'Access-Control-Allow-Origin': '*', // Allow requests from any origin
  },
});

// Export the Axios instance for use in other parts of the application
export default Api;
