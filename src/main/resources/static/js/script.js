
const togglesidebar =()=>{
	
	
	if($(".sidebar").is(":visible")){
		
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		
			}	
	else{
		
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};


const search=()=>{
	
	let query=$("#search-input").val();
	
	if(query==""){
		$(".search-result").hide();
	}
	else{
		
		
		let url=`http://localhost:3000/search/${query}`;
		
		fetch(url).then((response)=>{
			
			return response.json();
		}).then((data)=>{
			
			let text=`<div class='list-group'>`;
			
			
			data.forEach((contact)=>{
				
				text+=`<a href='/user/contact/${contact.cid}' class='list-group-item list-group-action'>${contact.name}</a>`;
			});
			text +=`</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
		});
		
		
	}
}

const paymentStart=()=>{
	
  let amount=$("#payment").val();
    if(amount=="" || amount==null){
	swal("Amount is Required!");
}

else{
	
	$.ajax({
		
		url:'/user/create_order',
		data:JSON.stringify({amount:amount,info:'order_request'}),
		contentType:'application/json',
		type:'POST',
		dataType:'json',
		success:function(response){
			
			console.log(response);
			
			if(response.status=="created"){
				
				let options={
				
				   key:'rzp_test_iQaJNXH48UDbcP',
	               amount:response.amount,
                   currency:response.currency,
                   name:'Smart Contact Manager',
                   description:'Donation',
                   image:'https://th.bing.com/th/id/OIP.hzvdg1Q5jqEDTx2Bzg9FqAHaEK?w=284&h=180&c=7&r=0&o=5&pid=1.7',
                   order_id:response.id,
                   handler:function(response){
	               console.log(response.razorpay_payment_id);
                   console.log(response.razorpay_order_id);
                   console.log(response.razorpay_signature);
                   console.log("payment Successful");

                    updatePaymentonServer(response.razorpay_payment_id,response.razorpay_order_id,'paid');
                   
                 },

                   prefill: {
                   name: "Md Ashraf Alam",
                   email: "alamashraf356@gmail.com",
                   contact: "9875356772"
                    },
                     notes: {
                         address: "Hyderabad"
                             },
                    theme: {
                           color: "#3399cc"
                           }
				};
				
				let rzp=new Razorpay(options);
				
				rzp.on('payment.failed',function(response){
					
					console.log(response.error.code);
		            console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
					swal("Failed!", "Payment Failed!", "error");
				});
				rzp.open();
				
			}
		},
		error:function(error){
			
			console.log(error);
		}
		
		
	})
	
	
}
}

function updatePaymentonServer(payment_id,order_id,status){
	
	$.ajax({
		
		url:'/user/update-order',
		data:JSON.stringify({payment_id:payment_id,order_id:order_id,status:status}),
		contentType:'application/json',
		type:'POST',
		dataType:'json',
		success:function(response){
			swal("Good job!", "Payment Successful", "success");
		},
		error:function(error){
		swal("Failed!", "Your Payment is Successful,but we didnot get on server,we will contact you as soon as possible", "error");	
		},
		
	});
	
}