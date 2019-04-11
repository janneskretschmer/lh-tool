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
import { fetchUser } from '../actions/user';

const styles = theme => ({
  root: {
    width: '100%',
    maxWidth: 360,
    backgroundColor: theme.palette.background.paper,
  },
});

class CheckboxList extends React.Component {
  state = {
    checked: [],
  };

  handleToggle = value => () => {
    const { checked } = this.state;
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    this.setState({
      checked: newChecked,
    });
  };

  render() {
    const { classes } = this.props;

    return (
      <List className={classes.root}>
        {props.need.users.map(value => {
          const user = fetchUser(value.userId);
          return (
            <ListItem key={value} role={undefined} dense button onClick={this.handleToggle(value)}>
              <Checkbox
                checked={this.state.checked.indexOf(value) !== -1}
                tabIndex={-1}
                disableRipple
              />
              <ListItemText primary={`${user.lastName}, ${user.firstName}`} />
            </ListItem>
          )})}
      </List>
    );
  }
}

CheckboxList.propTypes = {
  classes: PropTypes.object.isRequired,
};

export default requiresLogin(withStyles(styles)(CheckboxList));