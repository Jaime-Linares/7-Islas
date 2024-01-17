import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";
import ReactPaginate from "react-paginate";


const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [alerts, setAlerts] = useState([]);
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );

  useEffect(() => {
    fetchUsers();
  }, [currentPage, pageSize]);

  const fetchUsers = async () => {
    try {
      const response = await fetch(
        `/api/v1/users?&page=${currentPage}&pageSize=${pageSize}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        }
      }
      );
      const data = await response.json();

      setUsers(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  const handlePageChange = (selectedPage) => {
    setCurrentPage(selectedPage.selected);
  };

  const handlePageSizeChange = (event) => {
    const newSize = parseInt(event.target.value, 10);
    setPageSize(newSize);
    setCurrentPage(0);
  };

  const mapeo = (user) => {
    let row;
  
    if (user.authority.authority === "PLAYER") {
      row = (
        <tr key={user.id}>
          <td>{user.username}</td>
          <td>{user.authority.authority}</td>
          <td>
            <ButtonGroup>
              <Button
                size="sm"
                color="primary"
                aria-label={"edit-" + user.id}
                tag={Link}
                to={"/users/" + user.id}
              >
                Edit
              </Button>
              <Button
                size="sm"
                color="danger"
                aria-label={"delete-" + user.id}
                onClick={() =>
                  deleteFromList(
                    `/api/v1/users/${user.id}`,
                    user.id,
                    [users, setUsers],
                    [alerts, setAlerts],
                    setMessage,
                    setVisible
                  )
                }
              >
                Delete
              </Button>
            </ButtonGroup>
          </td>
        </tr>
      );
    } else {
      row = (
        <tr key={user.id}>
          <td>{user.username}</td>
          <td>{user.authority.authority}</td>
        </tr>
      );
    }
  
    return row;
  };

  const userList = Array.isArray(users)? users.map((user) => mapeo(user)) : users.content.map((user) => mapeo(user))

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <div className="admin-page-container-head">
        <h1 className="text-center">Users</h1>
        <div>
          <label>Items per page:</label>
          <select onChange={handlePageSizeChange} value={pageSize}>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </select>
        </div>
        {alerts.map((a) => a.alert)}
        {modal}
      </div>
      <div>
        <table className="table-admin">
          <thead>
            <tr>
              <th style={{ fontSize: 25, width: "15%" }}>Username</th>
              <th style={{ fontSize: 25, width: "15%" }}>Authority</th>
              <th style={{ fontSize: 25, width: "15%" }}>Actions</th>
            </tr>
          </thead>
          <tbody>{userList}</tbody>
        </table>
      </div>
      <div className="admin-paginacion">
        <ReactPaginate
          pageCount={totalPages}
          pageRangeDisplayed={5}
          marginPagesDisplayed={2}
          onPageChange={handlePageChange}
          containerClassName={'pagination'}
          activeClassName={'active'}
        />
      </div>
    </div>
  );
}
