import React, { useState, useEffect } from 'react';
import { Navbar, NavbarBrand, NavLink, NavItem, Nav, NavbarToggler, Collapse } from 'reactstrap';
import { Link } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";


function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);

    const toggleNavbar = () => setCollapsed(!collapsed);

    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
        }
    }, [jwt])

    let adminLinks = <></>;
    let playerLinks = <></>;
    let userLinks = <></>;
    let userLogout = <></>;
    let publicLinks = <></>;

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="users" tag={Link} to="/users">Users</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="players" tag={Link} to="/players">Players</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="listGamesA" tag={Link} to="/gamesAdmin">Games</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="achievementsA" tag={Link} to="/achievementsAdmin">Achievements</NavLink>
                    </NavItem>
                </>
            )
        }
        if (role === "PLAYER") {
            playerLinks = (
                <>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="homePlayer" tag={Link} to="/homePlayer">Home</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="stats" tag={Link} to="/stats">Stats</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="rankingGW" tag={Link} to="/ranking/gamesWon">Rankings</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="achievementsP" tag={Link} to="/achievementsPlayer">Achievements</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="social" tag={Link} to="/social">Friends</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="listGamesP" tag={Link} to="/gamesPlayer">Games</NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="backToGame" tag={Link} to="/backToGame">Reconnect</NavLink>
                    </NavItem>
                </>
            )
        }
    })

    if (!jwt) {
        publicLinks = (
            <>
                <NavItem className="d-flex">
                    <NavLink style={{ color: "white" }} id="register" tag={Link} to="/register">Register</NavLink>
                </NavItem>
                <NavItem className="d-flex">
                    <NavLink style={{ color: "white" }} id="login" tag={Link} to="/login">Login</NavLink>
                </NavItem>
            </>
        )
    } else {
        userLinks = (
            <>

            </>
        )
        userLogout = (
            <>
                <NavItem className="d-flex">
                    <NavLink style={{ color: "white" }} id="profile" tag={Link} to="/profile">{username}</NavLink>
                </NavItem>
                <NavItem className="d-flex">
                    <NavLink style={{ color: "white" }} id="logout" tag={Link} to="/logout">Logout</NavLink>
                </NavItem>
            </>
        )

    }

    return (
        <div>
            <Navbar expand="md" dark color="dark">
                <NavbarBrand href="/">
                    <img alt="logo" src="logoHome.png" style={{ height: 40, width: 40 }} />
                    7islas
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {userLinks}
                        {adminLinks}
                        {playerLinks}
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        {publicLinks}
                        {userLogout}
                    </Nav>
                </Collapse>
            </Navbar>
        </div>
    );
}

export default AppNavbar;