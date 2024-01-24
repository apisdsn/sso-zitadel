import { Navigate } from "react-router-dom";

import React, { useEffect } from "react";

const Callback = ({ auth, setAuth, userManager }) => {
  useEffect(() => {
    const handleCallback = async () => {
      try {
        await userManager.signinRedirectCallback().then((user) => {
          console.log("User after signinRedirectCallback: ", user);
          if (user) {
            setAuth(true);
          }
        });
      } catch (error) {
        setAuth(false);
      }
    };
    handleCallback();
  }, [auth, setAuth]);

  return <div>{auth && <Navigate to="/" />}</div>;
};

export default Callback;
