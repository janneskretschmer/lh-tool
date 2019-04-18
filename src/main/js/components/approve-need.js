import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Checkbox from '@material-ui/core/Checkbox';
import IconButton from '@material-ui/core/IconButton';
import CommentIcon from '@material-ui/icons/Comment';
import { requiresLogin } from '../util';
import { fetchUser } from '../actions/user';
import { changeApplicationStateForNeed } from '../actions/need';


const styles = theme => ({
  root: {
    width: '100%',
    maxWidth: 360,
    backgroundColor: theme.palette.background.paper,
  },
});

class ApplicationList extends React.Component {
	constructor(props) {
		super(props)
	  this.state = {
	    users: [],
	  };
	  const self = this;
    if (props.need.users) {
      props.need.users.forEach(user => fetchUser({ accessToken: props.accessToken, userId: user.userId }).then(result => {
        if (!result) {
          self.setState({
            users: [...self.state.users, { ...result.response, state: user.state }].sort(
              (a, b) => {
                if (a.lastName === b.lastName) {
                  return a.firstName < b.firstName ? 1 : -1;
                }
                return a.lastName < b.lastName ? 1 : -1;
              }),
          })
        }
      }));
    }
  }

  handleToggle = value => {
	  const newState = value.state === 'APPROVED' ? 'APPLIED' : 'APPROVED'
	  value.state = newState	  
    this.setState({
      users: this.state.users.map(user => user.id === value.id ? value : user),
    });
	  changeApplicationStateForNeed({ 
    	accessToken:this.props.accessToken,
    	userId:value.id,
    	needId:this.props.need.id,
    	state: newState,
    	handleFailure: err => console.log(err)
    })
  };

  render() {
    const { classes, need } = this.props;
    return need.users ? (
      <List className={classes.root}>
        {this.state.users.map(user => {
          return (
            <ListItem key={user} role={undefined} dense button onClick={() => this.handleToggle(user)}>
              <Checkbox
                checked={user.state === 'APPROVED'}
                tabIndex={-1}
                disableRipple
              />
              <ListItemText primary={`${user.lastName}, ${user.firstName}`} />
            </ListItem>
          )})}
      </List>
    ) : null;
  }
}

ApplicationList.propTypes = {
  classes: PropTypes.object.isRequired,
};

export default requiresLogin(withStyles(styles)(ApplicationList));