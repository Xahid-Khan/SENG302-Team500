const interval = setInterval(function sync () {
  console.log("Running token test")
  const res = fetch("api/v1/validate_token")
  console.log(res);
  if (!res.ok) {
    console.log("Token out of sync, reloading!")
    window.location.reload();
  }
}, 5000)