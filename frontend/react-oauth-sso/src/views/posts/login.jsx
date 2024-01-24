import { Navigate } from "react-router-dom";
import { useState } from "react";
import { Container, Card, Row, Col, Form, Button } from "react-bootstrap";
import { Facebook, Twitter, Google, Github } from "react-bootstrap-icons";

import ZitadelLogoLight from "./Zitadel";
import "./Login.css";

const Login = ({ auth, handleLogin }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLoginClick = () => {
    handleLogin(email, password);
  };
  return (
    <Container fluid className="my-5">
      {auth === null && <div>Loading...</div>}
      {auth === false && (
        <Row className="g-0 align-items-center justify-content-center">
          <Col xs="auto">
            <Card
              className="my-5"
              style={{
                background: "hsla(0, 0%, 100%, 0.55)",
                backdropFilter: "blur(30px)",
              }}
            >
              <Card.Body className="p-5 shadow-5 text-center">
                <h2 className="fw-bold mb-5">I2DEV</h2>

                <Form>
                  <Form.Group className="mb-4">
                    <Form.Label>Email</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="Enter your email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                  </Form.Group>

                  <Form.Group className="mb-4">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder="Enter your password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    />
                  </Form.Group>

                  <Form.Group className="mb-4 d-flex justify-content-center">
                    <Form.Check
                      type="checkbox"
                      label="Subscribe to our newsletter"
                    />
                  </Form.Group>

                  <Button
                    className="w-100 mb-4"
                    size="md"
                    variant="primary"
                    style={{
                      backgroundColor: "white",
                      borderColor: "blue",
                      color: "black",
                    }}
                    onClick={handleLoginClick}
                  >
                    Login
                  </Button>
                </Form>

                <div className="text-center">
                  <p>or sign up with:</p>

                  <Button
                    className="w-100 mb-4 d-flex align-items-center justify-content-center"
                    size="md"
                    variant="primary"
                    style={{
                      backgroundColor: "white",
                      borderColor: "red",
                      color: "black",
                    }}
                    onClick={handleLogin}
                  >
                    <span className="me-2">Login With Zitadel</span>
                    <ZitadelLogoLight size="10" />
                  </Button>

                  <Button
                    tag="a"
                    href="#"
                    variant="light"
                    className="mx-3"
                    style={{ color: "#1266f1" }}
                  >
                    <Facebook size="20" />
                  </Button>

                  <Button
                    tag="a"
                    href="#"
                    variant="light"
                    className="mx-3"
                    style={{ color: "#1266f1" }}
                  >
                    <Twitter size="20" />
                  </Button>

                  <Button
                    tag="a"
                    href="#"
                    variant="light"
                    className="mx-3"
                    style={{ color: "#1266f1" }}
                  >
                    <Google size="20" />
                  </Button>

                  <Button
                    tag="a"
                    href="#"
                    variant="light"
                    className="mx-3"
                    style={{ color: "#1266f1" }}
                  >
                    <Github size="20" />
                  </Button>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
      {auth && <Navigate to="/" />}
    </Container>
  );
};

export default Login;
