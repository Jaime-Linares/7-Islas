import "../static/css/player/Profile.css";
import tokenService from "../services/token.service";
import AdminProfileComponent from "./adminProfileComponent";
import PlayerProfileComponent from "./playerProfileComponent";


export default function HomePlayer() {

  async function handleSubmitEdit({ values }) {
    window.location.href = "/profile/edit";
  }
  async function handleSubmitDeleteAccount({ values }) {
    window.location.href = "/delete";
  }
  async function handleSubmitLogout({ values }) {
    window.location.href = "/logout";
  } 

  return (
    <div className="container-page">
      <div className="player-profile-container">
        
        <div className="player-profile-container2">
          {tokenService.getUser().roles[0]==="ADMIN" && <AdminProfileComponent/>}
          {tokenService.getUser().roles[0]==="PLAYER" && <PlayerProfileComponent/>}
        </div>

        <div className="ButtonPartContainer">
          <button onClick={handleSubmitEdit} className="button">
            Edit profile
          </button>
          <button onClick={handleSubmitLogout} className="button">
            Logout
          </button>
          <button onClick={handleSubmitDeleteAccount} className="buttonDelete">
            Delete Account
          </button>
        </div>

       </div>
    
    </div>
  );
}
