import Select, { components } from 'react-select';
import React from 'react';
import { SessionContext } from '../../providers/session-provider';
import { ItemsContext } from '../../providers/items-provider';
import WithPermission from '../with-permission';
import WithoutPermission from '../without-permission';
import { Chip, withStyles, CircularProgress } from '@material-ui/core';

const styles = theme => ({
    chip: {
        marginRight: theme.spacing.unit,
        marginTop: theme.spacing.unit,
    },
});

const ItemTagsComponent = props => (
    <>
        <ItemsContext.Consumer>
            {itemsState => itemsState.getSelectedItem().tags ? (
                <>
                    <WithPermission permission="ROLE_RIGHT_ITEMS_POST">
                        <Select
                            inputValue={itemsState.tag}
                            options={itemsState.tags}
                            value={itemsState.getSelectedItem().tags}
                            isMulti
                            isClearable={false}
                            isDisabled={itemsState.actionsDisabled}
                            placeholder="Schlagwörter können eingegeben oder ausgewählt werden. Zum Speichern Leerzeichen eingeben."
                            noOptionsMessage={() => 'Bitte neues Schlagwort eingeben und mit Leerzeichen speichern.'}
                            name="tags"
                            classNamePrefix="select"
                            styles={{
                                multiValue: (provided, state) => ({
                                    ...provided,
                                    borderRadius: '16px',
                                })
                            }}
                            components={{
                                MultiValueRemove: props => {
                                    if (!props.data.id) {
                                        return (
                                            <components.MultiValueRemove {...props} isDisabled={true}>
                                                <CircularProgress size={14} />
                                            </components.MultiValueRemove>
                                        );
                                    }
                                    return (<components.MultiValueRemove {...props} />);
                                }
                            }}
                            onInputChange={text => itemsState.changeTag(text)}
                            onChange={(_, event) => {
                                if (event.action === 'select-option') {
                                    itemsState.saveTagName(event.option.value);
                                } else if (event.action === 'remove-value') {
                                    itemsState.deleteTag(event.removedValue);
                                }
                            }}
                        />
                    </WithPermission>
                    <WithoutPermission permission="ROLE_RIGHT_ITEMS_POST">
                        {itemsState.getSelectedItem().tags.map(tag => (
                            <Chip key={tag.name} label={tag.name} className={props.classes.chip} />
                        ))}
                    </WithoutPermission>
                </>
            ) : (<CircularProgress />)}
        </ItemsContext.Consumer>
    </>
);
export default withStyles(styles)(ItemTagsComponent);