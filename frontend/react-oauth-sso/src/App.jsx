import { Link } from "react-router-dom";
import { UserManager, WebStorageStateStore } from "oidc-client-ts";
import authConfig from "./authConfig";
import Routes from "./routes";
import { useMemo } from "react";

export default function App() {
  const userManager = useMemo(
    () =>
      new UserManager({
        userStore: new WebStorageStateStore({ store: window.localStorage }),
        ...authConfig,
      })
  );

  const handleLogout = () => {
    userManager.signoutRedirect();
  };
  return (
    <>
      <div>
        <nav className="navbar navbar-expand-lg bg-dark" data-bs-theme="dark">
          <div className="container">
            <Link to="/" className="navbar-brand">
              HOME
            </Link>
            <button
              className="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarSupportedContent"
              aria-controls="navbarSupportedContent"
              aria-expanded="false"
              aria-label="Toggle navigation"
            >
              <span className="navbar-toggler-icon" i>
                <i class="ri-list-check"></i>
              </span>
            </button>
            <div
              className="collapse navbar-collapse"
              id="navbarSupportedContent"
            >
              <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                <li className="nav-item">
                  <Link
                    to="/posts"
                    className="nav-link active"
                    aria-current="page"
                  >
                    POSTS
                  </Link>
                </li>
              </ul>
              <ul className="navbar-nav ms-auto mb-2 mb-lg-0" role="search">
                <li className="nav-item">
                  <button
                    className="btn btn-success ms-2"
                    onClick={handleLogout}
                  >
                    LOGOUT
                  </button>
                </li>
                <li className="nav-item">
                  <a href="/login" className="btn btn-success ms-2">
                    LOGIN
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </nav>
      </div>

      <Routes />
    </>
  );
}
