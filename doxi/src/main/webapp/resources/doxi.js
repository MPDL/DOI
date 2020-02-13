		function logout() {
			$.ajax({
				type: "GET",
				url: "/doxi/doi/rest/v1",
				dataType: 'json',
				async: true,
				username: "user",
				password: "any_stupid_password"
			})
				
			//In our case, we WANT to get access denied, so a success would be a failure.
			.done(function(){
				alert("Error while logging out");
			})
			
			//Likewise, a failure *usually* means we succeeded.
			//set window.location to redirect the user to wherever you want them to go
			.fail(function(data){
				if(data.status==401) {
					alert("Logout successful");
				}
				else {
					alert("Error while logging out");
				}
				//window.location = "/doxi";
			});
		}