import React from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import Login from "./auth/login";
import Logout from "./auth/logout";
import tokenService from "./services/token.service";
import HomePlayer from "./player/home";
import Rules from "./rules/rules";
import EditProfile from "./profile/editProfile";
import Profile from "./profile/index";
import Achievements from "./player/achievements";
import Stats from "./player/stats";
import Social from "./player/social/social";
import Delete from "./auth/delete";
import UserListAdmin from "./admin/users/userListAdmin";
import PlayerListAdmin from "./admin/players/playerListAdmin";
import GameWaitingRoom from "./game/gameWaitingRoom";
import GameCodeRoom from "./game/gameCodeRoom";
import GameListAdmin from "./admin/games/gameListAdmin";
import GameListPlayer from "./player/games/gameListPlayer";
import GameRoom from "./game/gameRoom";
import AchievementsListAdmin from "./admin/achievements/achievementsListAdmin";
import AchievementsEditAdmin from "./admin/achievements/achievementsEditAdmin";
import CreateAchievementAdmin from "./admin/achievements/createAchievementAdmin";
import EndGameRoom from "./game/endGameRoom";
import CreateRegisterPlayer from "./components/crud/createRegisterPlayer";
import EditAdminProfileAndUsers from "./components/crud/editAdminProfileAndUsers";
import EditPlayerProfileAndPlayers from "./components/crud/editPlayerProfileAndPlayers";
import RankingGamesWon from "./player/rankings/rankingGamesWon";
import RankingAverageScore from "./player/rankings/rankingAverageScore";
import BackToGame from "./player/reconnect/backToGame";


function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let adminRoutes = <></>;
  let playerRoutes = <></>;
  let userRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:id" exact={true} element={<PrivateRoute><EditAdminProfileAndUsers /></PrivateRoute>} />
          <Route path="/players" exact={true} element={<PrivateRoute><PlayerListAdmin /></PrivateRoute>} />
          <Route path="/players/:id" exact={true} element={<PrivateRoute><EditPlayerProfileAndPlayers /></PrivateRoute>} />
          <Route path="/players/new" exact={true} element={<PrivateRoute><CreateRegisterPlayer /></PrivateRoute>} />
          <Route path="/gamesAdmin" element={<PrivateRoute><GameListAdmin /></PrivateRoute>} />
          <Route path="/achievementsAdmin" exact={true} element={<PrivateRoute><AchievementsListAdmin /></PrivateRoute>} />
          <Route path="/achievements/:id" exact={true} element={<PrivateRoute><AchievementsEditAdmin /></PrivateRoute>} />
          <Route path="/achievements/new" exact={true} element={<PrivateRoute><CreateAchievementAdmin /></PrivateRoute>} />
        </>
      )
    }
    if (role === "PLAYER") {
      playerRoutes = (
        <>
          <Route path="/homePlayer" exact={true} element={<PrivateRoute><HomePlayer /></PrivateRoute>} />
          <Route path="/achievementsPlayer" exact={true} element={<PrivateRoute><Achievements /></PrivateRoute>} />
          <Route path="/stats" exact={true} element={<PrivateRoute><Stats /></PrivateRoute>} />
          <Route path="/social" exact={true} element={<PrivateRoute><Social /></PrivateRoute>} />
          <Route path="/game/:id/waitingRoom" exact={true} element={<PrivateRoute><GameWaitingRoom /></PrivateRoute>} />
          <Route path="/gameCodeRoom" exact={true} element={<PrivateRoute><GameCodeRoom /></PrivateRoute>} />
          <Route path="/gamesPlayer" exact={true} element={<PrivateRoute><GameListPlayer /></PrivateRoute>} />
          <Route path="/gameRoom/:id" exact={true} element={<PrivateRoute><GameRoom /></PrivateRoute>} />
          <Route path="/game/:id/endRoom" exact={true} element={<PrivateRoute><EndGameRoom /></PrivateRoute>} />
          <Route path="/ranking/gamesWon" exact={true} element={<PrivateRoute><RankingGamesWon /></PrivateRoute>} />
          <Route path="/ranking/averageScore" exact={true} element={<PrivateRoute><RankingAverageScore /></PrivateRoute>} />
          <Route path="/backToGame" exact={true} element={<PrivateRoute><BackToGame /></PrivateRoute>} />
        </>
      )
    }
  })

  if (!jwt) {
    publicRoutes = (
      <>
        <Route path="/register" element={<CreateRegisterPlayer />} />
        <Route path="/login" element={<Login />} />
      </>
    )
  } else {
    userRoutes = (
      <>
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
        <Route path="/delete" element={<Delete />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/profile/edit" element={<EditProfile />} />
        <Route path="/rules" element={<Rules />} />
      </>
    )
  }

  return (
    <div>
      <ErrorBoundary FallbackComponent={ErrorFallback} >
        <AppNavbar />
        <Routes>
          <Route path="/" exact={true} element={<Home />} />
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
          {playerRoutes}
        </Routes>
      </ErrorBoundary>
    </div>
  );
}

export default App;