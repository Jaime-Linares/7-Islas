import tokenService from "../../../services/token.service";
import deleteFromList from "../../../util/deleteFromList";
import { Button } from "reactstrap";
import useIntervalFetchState from "../../../util/useIntervalFetchState"
import { useState } from 'react';
import getErrorModal from "../../../util/getErrorModal";

export default function Friends() {
  const user = tokenService.getUser();
  const jwt = tokenService.getLocalAccessToken();

  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [alerts, setAlerts] = useState([]);

  const [friends, setFriends] = useIntervalFetchState(
    [],
    `/api/v1/social/${user.id}`,
    jwt,
    setMessage,
    setVisible,
    null,
    1000
  );

  const deleteFriend = function (value) {
    deleteFromList(
      `/api/v1/social/${value}`,
      value,
      [friends, setFriends],
      [alerts, setAlerts],
      setMessage,
      setVisible
    );
  }

  const modal = getErrorModal(setVisible, visible, message);

  const friendsList = friends.map((friend) => {
    return (
      <>
        {modal}
        <div>{(friend.player1.user.id === user.id && friend.player2.user.username) ||
          (friend.player2.user.id === user.id && friend.player1.user.username)}</div>
        <div>
          <Button color="danger" onClick={() => deleteFriend(friend.id)}>
            Delete
          </Button>
        </div>
      </>
    )
  })

  return (
    <div className="table-social">
      <div>{friendsList}</div>
    </div>
  )
}