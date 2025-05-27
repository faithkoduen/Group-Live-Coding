function editTasks( task){
    let deadlines= [1,4,2,3];
    console.log(deadlines);
    for(let i=0; i <deadlines.length;++i){
      if(deadlines[i]>4){
        console.log(task.pop());
      }
      else if(deadlines.length<=2){
        console.log(task.push(signin));
      }
      else{
        console.log("You can edit your task");
      }
    }
}
const task= (['sign up','login','enter password','reset_passsword']);
editTasks(task);
console.log(task);
function priorities(Priority){
    Priority.forEach((priority)=>{
        switch(priority){
            case 'high':
                console.log(`This task has high priority`);
                break;
                case 'medium':
                    console.log(`This task has medium priority`);
                    break;
                    case 'low':
                        console.log(`This task has low priority`);
                        break;
                        default:
                            console.log(`You will have another priority by tommorow`);
        }
    })
}
const Priority=['high','low','medium','none'];
priorities(Priority);