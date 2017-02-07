import swal from "sweetalert2";
import * as Api from "../api";

const deposit = (to) => () => {
    swal({
        title: "Deposit!",
        text: "Type in the amount",
        input: "text",
        showCancelButton: true,
        animation: "slide-from-top",
        inputPlaceholder: "amount"
    }).then(function (inputValue) {
        if (inputValue === false) return false;

        if (inputValue === "") {
            swal.showInputError("You need to type in something!");
            return false
        }

        Api.createDonation({to, value: inputValue});

        swal("Nice!", "You deposited: " + inputValue, "success");
    });
};

export default deposit