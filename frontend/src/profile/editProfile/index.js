import tokenService from "../../services/token.service";
import EditAdminProfileAndUsers from "../../components/crud/editAdminProfileAndUsers";
import EditPlayerProfileAndPlayers from "../../components/crud/editPlayerProfileAndPlayers";

export default function EditProfile() {
  return (
    <>
      {tokenService.getUser() && tokenService.getUser().roles[0] === "ADMIN" && <EditAdminProfileAndUsers />}
      {tokenService.getUser() && tokenService.getUser().roles[0] === "PLAYER" && <EditPlayerProfileAndPlayers />}
    </>
  );
}