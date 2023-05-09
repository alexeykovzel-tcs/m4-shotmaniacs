const wrapper = document.querySelector('.cards-wrapper');
const dots = document.querySelectorAll('.dot');
let activeDotNum = 0;


/*////////////////////////////
     UNCOMMENT TO ADD DOTS
//////////////////////////////


//
//
//
// dots.forEach((dot, idx) => {
//     // number each dot according to array index
//     dot.setAttribute('data-num', idx);
//     // add a click event listener to each dot
//     dot.addEventListener('click', (e) => {
//         console.log("clicked")
//         let clickedDotNum = e.target.dataset.num;
//         // if the dot clicked is already active, then do nothing
//         if (clickedDotNum !== activeDotNum) {
//             // shift the wrapper
//             let displayArea = wrapper.parentElement.clientWidth;
//             // let pixels = -wrapper.clientWidth * clickedDotNum;
//             let pixels = -displayArea * clickedDotNum
//             wrapper.style.transform = 'translateX(' + pixels + 'px)';
//             // remove the active class from the active dot
//             dots[activeDotNum].classList.remove('active');
//             // add the active class to the clicked dot
//             dots[clickedDotNum].classList.add('active');
//             // now set the active dot number to the clicked dot;
//             activeDotNum = clickedDotNum;
//         }
//     });
// });*///