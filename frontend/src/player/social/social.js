import "../../static/css/player/social.css";
import React, { useState } from 'react';
import getErrorModal from "../../util/getErrorModal";
import Friends from "./components/friends"
import FriendRequests from "./components/friendRequests"
import GameInvitations from "./components/gameInvitations"
import AddFriend from "./components/addFriend";

export default function Social() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="player-social-background">
      <div className="player-social-head">
        <h1>SOCIAL</h1>
        {modal}
      </div>

      <div className="player-social-principal-grid">
        <div className="player-social-p1">
          <h2>Friends</h2>
          <div><Friends /></div>
        </div>
        <div className="player-social-p2">
          <h2>Friends Requests</h2>
          <div><FriendRequests /></div>
        </div>
        <div className="player-social-p3">
          <h2>Game Invitations</h2>
          <div><GameInvitations /></div>
        </div>
      </div>

      <AddFriend />

    </div>
  );
}
