import tokenService from "../../../services/token.service";
import deleteFromList from "../../../util/deleteFromList";
import { Button } from "reactstrap";
import useIntervalFetchState from "../../../util/useIntervalFetchState"
import { useState } from 'react';
import getErrorModal from "../../../util/getErrorModal";


export default function FriendRequests() {
  const user = tokenService.getUser();
  const jwt = tokenService.getLocalAccessToken();

  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [alerts, setAlerts] = useState([]);

  const [friendRequests, setFriendRequests] = useIntervalFetchState(
    [],
    `/api/v1/social/${user.id}/requests`,
    jwt,
    setMessage,
    setVisible,
    null,
    1000
  );

  const acceptRequest = function (value) {
    fetch(`/api/v1/social/${value.id}/acceptRequest`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      }
    })
      .then((response) => {
        return response.text();
      })
      .catch((err) => {
        console.log(err);
        alert("Error accepting request")
      });
  }

  const rejectRequest = function (value) {
    deleteFromList(
      `/api/v1/social/${value}`,
      value,
      [friendRequests, setFriendRequests],
      [alerts, setAlerts],
      setMessage,
      setVisible
    );
  }

  const friendRequestsList = friendRequests.map((friendRequest) => {
    return (
      <>
        <div>{friendRequest.player1.user.username}</div>
        <div className="horizontal">
          <Button onClick={() => acceptRequest(friendRequest)}>
            ACCEPT
          </Button>
          <Button color="danger" onClick={() => rejectRequest(friendRequest.id)}>
            REJECT
          </Button>
        </div>
      </>
    )
  });

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="table-social">
      {modal}
      <div>{friendRequestsList}</div>
    </div>
  )
}